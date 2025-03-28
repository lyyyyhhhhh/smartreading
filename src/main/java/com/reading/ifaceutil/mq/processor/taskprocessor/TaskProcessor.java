package com.reading.ifaceutil.mq.processor.taskprocessor;

import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.mq.processor.Processor;
import com.reading.ifaceutil.repository.TaskRulesRepository;
import com.reading.ifaceutil.service.UserTaskProgressService;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class TaskProcessor implements Processor {
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    @Lazy
    private Map<String, TaskBaseProcessor> taskProcessors; // 自动注入所有 TaskProcessor
    @Autowired
    private UserTaskProgressService userTaskProgressService;

    @Override
    public void Distribute(Map<String, Object> message) {
        // 1. 整理message里的基本数据, 获取需要的值
        Map<String, Object> requiredData = assemble(message);
        // 2. 同步处理业务逻辑 | 异步处理业务逻辑 + 重试机制
        handleTask(requiredData);
    }

    private Map<String, Object> assemble(Map<String, Object> message) {
        Map<String, Object> requiredData = new HashMap<>();
        String actionType = (String) message.get("actionType");

        if (AppConstants.SIGN_IN.equals(actionType)) {
            requiredData.put("signIn", true);
        } else if (AppConstants.COMMENT.equals(actionType)) {
            requiredData.put("comment", message.get("comment").toString());
        } else if(AppConstants.READING.equals(actionType)) {
            requiredData.put("addedTime", message.get("addedTime"));
        }

        requiredData.put("userId", message.get("userId"));
        requiredData.put("actionType", actionType);
        return requiredData;
    }

    private void handleTask(Map<String, Object> requiredData) {
        Long userId = Long.parseLong(requiredData.get("userId").toString());
        String actionType = (String) requiredData.get("actionType");
        LocalDate currentDate = LocalDate.now();

        // 获取签到任务的最大尝试次数
        TaskRules taskRule = taskRulesRepository.findByTaskType(actionType);
        int maxAttempts = taskRule != null ? taskRule.getMaxAttempts() : 1;

        // 查询当前用户当天的任务
        List<UserTaskProgress> tasks = userTaskProgressService.getSpecifiedTasks(userId, currentDate, actionType);
        int taskCount = tasks.size();

        // 查找第一个 PENDING 任务
        Optional<UserTaskProgress> pendingTask = tasks.stream()
                .filter(task -> AppConstants.PENDING.equals(task.getStatus()))
                .findFirst();
        if (pendingTask.isEmpty() && taskCount >= maxAttempts) {
            log.info("用户{} 今日已完成所有{}任务", userId, AppConstants.taskToShow.get(actionType));
            return;
        }
        UserTaskProgress newTask = pendingTask.orElseGet(() -> userTaskProgressService.createNewTask(userId, actionType));

        // 获取对应类型的任务processor
        TaskBaseProcessor processor = taskProcessors.get((AppConstants.taskTypeToProcessorMap.get(actionType)));
        while (requiredData != null) {
            // 3. 整理拼接业务数据
            processor.assemble(newTask, requiredData);
            // 4. 业务逻辑
            // requiredData在READING场景下会再次用到
            requiredData = processor.updateTask(newTask, taskRule, userId);
            if (!AppConstants.COMPLETED.equals(newTask.getStatus())) {
                log.info("用户{}，条件暂不匹配, {}", userId, pendingTask);
                break;
            }
            if (taskCount >= maxAttempts) {
                log.info("用户{} 今日已完成所有{}任务", userId, AppConstants.taskToShow.get(actionType));
                break;
            } else {
                newTask = userTaskProgressService.createNewTask(userId, actionType);
                taskCount++;
            }
        }
    }
}
