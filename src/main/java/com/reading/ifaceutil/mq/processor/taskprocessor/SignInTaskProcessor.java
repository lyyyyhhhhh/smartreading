package com.reading.ifaceutil.mq.processor.taskprocessor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.repository.TaskRulesRepository;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.*;

@Slf4j
@Component
public class SignInTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;

    @Override
    public void assemble(UserTaskProgress task, Map<String, Object> requiredData) {
        try {
            log.info("required data: {}", requiredData);
            // 针对签到任务，获取连续签到次数
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);
            taskConditions.put("signIn", requiredData.get("signIn"));
            taskConditions.put("continuousNum", (Integer) taskConditions.get("continuousNum") + 1);
            task.setProgressJson(objectMapper.writeValueAsString(taskConditions));
            userTaskProgressRepository.save(task);
        } catch (Exception e) {
            log.error("Error processing SIGN_IN task", e);
        }
    }

    @Override
    public Map<String, Object> updateTask(UserTaskProgress task, TaskRules taskRule, Long userId) {
        try {
            Map<String, Object> ruleConditions = objectMapper.readValue(taskRule.getConditionJson(), Map.class);
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);

            boolean isCompleted = taskConditions.get("signIn") != null &&
                    taskConditions.get("signIn").equals(ruleConditions.get("signIn"));

            if (isCompleted) {
                task.setStatus(AppConstants.COMPLETED);
                userTaskProgressRepository.save(task);

                log.info("用户{} 签到任务已完成", userId);
            }
        } catch (Exception e) {
            log.error("Error updating SIGN_IN task", e);
        }
        return null;
    }

    @Override
    public UserTaskProgress createNewTask(Long userId) {
        return createCommonNewTask(userId, getTaskType(), initializeProgress(userId));
    }

    @Override
    public int calculatePoints(UserTaskProgress task) {
        TaskRules taskRule = taskRulesRepository.findByTaskType(task.getTaskType());
        int sumPoints = 0;
        // 解析 task 中的 progressJson
        try {
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);
            Map<String, Object> ruleConditions = objectMapper.readValue(taskRule.getConditionJson(), Map.class);
            Map<String, Integer> extraPointsRule = (Map<String, Integer>) ruleConditions.get("extra_points");
            int continuousNum = (Integer) taskConditions.get("continuousNum") % 7;
            int extraPoints =  extraPointsRule.get(Integer.toString(continuousNum));
            sumPoints = taskRule.getRewardPoints() + extraPoints;
        } catch (JsonProcessingException e) {
            log.info("JSON 解析出错: {}", e.getMessage());
        }
        return sumPoints;
    }

    private String initializeProgress(Long userId) {
        Map<String, Object> taskConditions = new HashMap<>();
        taskConditions.put("continuousNum", 0);
        taskConditions.put("signIn", false);

        // 查询前一天的签到记录
        List<UserTaskProgress> lastTasks = userTaskProgressRepository.findByUserIdAndCreateDateAndTaskType(
                userId, LocalDate.now().minusDays(1), AppConstants.SIGN_IN
        );
        Optional<UserTaskProgress> lastTask = lastTasks.stream()
                .filter(task -> !AppConstants.PENDING.equals(task.getStatus()))
                .max(Comparator.comparing(UserTaskProgress::getCreateTime));

        try {
            if (lastTask.isPresent()) {
                Map<String, Object> lastTaskConditions = objectMapper.readValue(lastTask.get().getProgressJson(), Map.class);
                if (lastTaskConditions.get("continuousNum") != null) {
                    taskConditions.put("continuousNum", lastTaskConditions.get("continuousNum"));
                }
            }
            return objectMapper.writeValueAsString(taskConditions);
        } catch (JsonProcessingException e) {
            log.error("JSON 解析出错: {}", e.getMessage());
            return "{}";
        }
    }

    private String getTaskType() {
        return AppConstants.SIGN_IN;
    }
}
