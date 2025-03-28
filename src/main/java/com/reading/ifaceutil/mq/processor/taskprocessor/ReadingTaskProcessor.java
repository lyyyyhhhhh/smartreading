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
import java.util.*;

@Slf4j
@Component
public class ReadingTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;

    @Override
    public void assemble(UserTaskProgress task, Map<String, Object> requiredData) {
        try {
            log.info("required data: {}", requiredData);
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);
            taskConditions.put("readTime", (Integer) taskConditions.get("readTime") + (Integer) requiredData.get("addedTime"));
            task.setProgressJson(objectMapper.writeValueAsString(taskConditions));
            userTaskProgressRepository.save(task);
        } catch (Exception e) {
            log.error("Error processing READING task", e);
        }
    }

    @Override
    public Map<String, Object> updateTask(UserTaskProgress task, TaskRules taskRule, Long userId) {
        // 更新任务状态逻辑
        try {
            Map<String, Object> ruleConditions = objectMapper.readValue(taskRule.getConditionJson(), Map.class);
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);
            Map<String, Object> readStagesRule = (Map<String, Object>) ruleConditions.get("time_stage");
            String stageIdx = taskConditions.get("stage_index").toString();
            Map<String, Integer> detailStage = (Map<String, Integer>) readStagesRule.get(stageIdx);
            boolean isCompleted = (Integer) taskConditions.get("readTime") >= detailStage.get("read_time");

            if (isCompleted) {
                Map<String, Object> requiredData = new HashMap<>();
                requiredData.put("addedTime", taskConditions.get("readTime"));
                taskConditions.put("readTime", detailStage.get("read_time"));
                task.setProgressJson(objectMapper.writeValueAsString(taskConditions));
                task.setStatus(AppConstants.COMPLETED);
                userTaskProgressRepository.save(task);
                log.info("用户{} 阅读任务阶段{}已完成", userId, stageIdx);
                return requiredData;
            }
        } catch (Exception e) {
            log.error("Error updating READING task", e);
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
            Map<String, Object> extraPointsRule = (Map<String, Object>) ruleConditions.get("time_stage");
            Integer stageIdx = (Integer) taskConditions.get("stage_index");
            // 从 extraPointsRule 中获取对应阶段的 Map
            Map<String, Object> stageInfo = (Map<String, Object>) extraPointsRule.get(stageIdx.toString());
            if (stageInfo != null) {
                // 从阶段信息中获取 points 值
                sumPoints = taskRule.getRewardPoints() + (Integer) stageInfo.get("points");
            }
        } catch (JsonProcessingException e) {
            log.info("JSON 解析出错: {}", e.getMessage());
        }
        return sumPoints;
    }

    private String initializeProgress(Long userId) {
        Map<String, Object> taskConditions = new HashMap<>();
        taskConditions.put("stage_index", 1);
        taskConditions.put("readTime", 0);

        // 查询今天的阅读记录
        List<UserTaskProgress> lastTasks = userTaskProgressRepository.findByUserIdAndCreateDateAndTaskType(
                userId, LocalDate.now(), AppConstants.READING
        );
        Optional<UserTaskProgress> lastTask = lastTasks.stream()
                .filter(task -> !AppConstants.PENDING.equals(task.getStatus()))
                .max(Comparator.comparing(UserTaskProgress::getCreateTime)
                        .thenComparing(UserTaskProgress::getId));


        try {
            if (lastTask.isPresent()) {
                Map<String, Object> lastTaskConditions = objectMapper.readValue(lastTask.get().getProgressJson(), Map.class);
                if (lastTaskConditions.get("stage_index") != null) {
                    taskConditions.put("stage_index", (Integer) lastTaskConditions.get("stage_index") + 1);
                }
            }
            return objectMapper.writeValueAsString(taskConditions);
        } catch (JsonProcessingException e) {
            log.error("JSON 解析出错: {}", e.getMessage());
            return "{}";
        }
    }

    private String getTaskType() {
        return AppConstants.READING;
    }
}
