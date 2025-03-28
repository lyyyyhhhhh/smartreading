package com.reading.ifaceutil.controller;

import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.model.dto.request.TaskClaimRequest;
import com.reading.ifaceutil.model.dto.response.TaskDetailResponse;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.service.UserPointsService;
import com.reading.ifaceutil.service.UserTaskProgressService;
import com.reading.ifaceutil.service.TaskRuleService;
import com.reading.ifaceutil.utils.constants.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class UserTasksController {
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;
    @Autowired
    private UserTaskProgressService userTaskProgressService;
    @Autowired
    private TaskRuleService taskRuleService;
    @Autowired
    private UserPointsService userPointsService;
    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/rules")
    public List<TaskRules> getTaskRules() {
        return taskRuleService.getTaskRules();
    }

    @GetMapping("/rule")
    public TaskRules getTaskRuleByType(@RequestParam String taskType) {
        return taskRuleService.findByTaskType(taskType);
    }

    @GetMapping("/status/{userId}")
    public List<UserTaskProgress> getUserTaskStatus(@PathVariable Long userId) {
        LocalDate currentDate = LocalDate.now();
        return userTaskProgressRepository.findByUserIdAndCreateDate(userId, currentDate);
    }

    @GetMapping("/ongoing")
    public UserTaskProgress getUserOngoingTask(@RequestParam Long userId, @RequestParam String taskType) {
        // 查询今天的任务记录
        List<UserTaskProgress> tasks = userTaskProgressRepository.findByUserIdAndCreateDateAndTaskType(
                userId, LocalDate.now(), taskType);
        Optional<UserTaskProgress> ongoingTask = tasks.stream()
                .filter(task -> AppConstants.PENDING.equals(task.getStatus()))
                .max(Comparator.comparing(UserTaskProgress::getCreateTime));
        if (ongoingTask.isPresent()) {
            return ongoingTask.get();
        }
        Optional<UserTaskProgress> latestTask = tasks.stream()
                .filter(task -> !AppConstants.PENDING.equals(task.getStatus()))
                .max(Comparator.comparing(UserTaskProgress::getCreateTime)
                        .thenComparing(UserTaskProgress::getId));
        return latestTask.get();
    }

    @GetMapping("/detail")
    public TaskDetailResponse getUserTaskDetail(@RequestParam Long userId, @RequestParam String taskType) {
        LocalDate currentDate = LocalDate.now();
        List<UserTaskProgress> tasks = userTaskProgressService.getSpecifiedTasks(userId, currentDate, taskType);
        TaskRules taskRule = taskRuleService.findByTaskType(taskType);
        int claimedTask = 0, completedTask = 0;
        int maxAttempt = taskRule.getMaxAttempts();
        String status = "";
        for (UserTaskProgress task : tasks) {
            if (AppConstants.PENDING.equals(task.getStatus())) {
                continue;
            }
            if (AppConstants.CLAIMED.equals(task.getStatus())) {
                claimedTask++;
            }
            completedTask++;
        }
        String progressJson = "{}";
        if (claimedTask < completedTask) {
            status = AppConstants.COMPLETED;
            UserTaskProgress curTask = userTaskProgressService.getByIdDateTypeStatDesc(userId, currentDate, taskType, AppConstants.COMPLETED).get(0);
            progressJson = curTask.getProgressJson();
        } else if (claimedTask == completedTask) {
            if (completedTask < maxAttempt) {
                status = AppConstants.PENDING;
                Optional<UserTaskProgress> curTask = userTaskProgressService.getSpecifiedTasks(userId, currentDate, taskType)
                        .stream().filter(task -> AppConstants.PENDING.equals(task.getStatus())).findFirst();
                progressJson = curTask.get().getProgressJson();
            } else {
                status = AppConstants.CLAIMED;
                UserTaskProgress curTask = userTaskProgressService.getByIdDateTypeStatDesc(userId, currentDate, taskType, AppConstants.CLAIMED).get(0);
                progressJson = curTask.getProgressJson();
            }
        }
        return new TaskDetailResponse(claimedTask, completedTask, status, progressJson);
    }

    @PostMapping("/claim")
    public ResponseEntity<String> claimTaskReward(@RequestBody TaskClaimRequest request) {
        LocalDate currentDate = LocalDate.now();
        List<UserTaskProgress> tasks = userTaskProgressRepository.findByUserIdAndCreateDateAndTaskTypeOrderById(request.getUserId(), currentDate, request.getTaskType());
        if (tasks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("尚未可领取任务");
        }
        UserTaskProgress completedTask = null;
        for (UserTaskProgress task : tasks) {
            if (AppConstants.COMPLETED.equals(task.getStatus())) {
                completedTask = task;
                break;
            }
        }
        if (completedTask == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("尚未可领取任务");
        }
        // 获取任务规则
        int points = taskRuleService.calculatePoints(completedTask);
        userPointsService.changePoints(request.getUserId(), points);
        String description = AppConstants.taskToShow.get(request.getTaskType()) + "任务领奖";
        kafkaProducer.sendRewardPointsEvent(request.getUserId(), AppConstants.USER_TASK_PROGRESS, completedTask.getId(), points, description);
        completedTask.setStatus(AppConstants.CLAIMED);
        userTaskProgressRepository.save(completedTask);
        return ResponseEntity.ok("任务奖励已领取");
    }

}
