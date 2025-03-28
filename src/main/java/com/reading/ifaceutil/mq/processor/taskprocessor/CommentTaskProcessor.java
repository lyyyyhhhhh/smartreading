package com.reading.ifaceutil.mq.processor.taskprocessor;

import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.repository.TaskRulesRepository;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CommentTaskProcessor extends AbstractTaskProcessor {
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;

    @Override
    public void assemble(UserTaskProgress task, Map<String, Object> requiredData) {
        try {
            // 获取任务规则和当前任务的进度信息
            TaskRules taskRule = taskRulesRepository.findByTaskType(AppConstants.COMMENT);
            Map<String, Object> ruleConditions = objectMapper.readValue(taskRule.getConditionJson(), Map.class);
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);

            String comment = (String) requiredData.get("comment");
            log.info("Rule conditions: {}, Task conditions: {}", ruleConditions, taskConditions);

            if (comment != null && comment.length() >= (Integer) ruleConditions.get("min_comment_length")) {
                // 如果评论长度符合要求，更新评论计数
                int commentCount = (Integer) taskConditions.getOrDefault("comment_count", 0);
                taskConditions.put("comment_count", commentCount + 1);
            }

            log.info("Updated task progress: {}", taskConditions);
            task.setProgressJson(objectMapper.writeValueAsString(taskConditions));
            userTaskProgressRepository.save(task);

        } catch (Exception e) {
            log.error("Error processing COMMENT task for taskId: {}", task.getId(), e);
        }
    }

    @Override
    public Map<String, Object> updateTask(UserTaskProgress task, TaskRules taskRule, Long userId) {
        // 更新任务状态逻辑
        try {
            Map<String, Object> ruleConditions = objectMapper.readValue(taskRule.getConditionJson(), Map.class);
            Map<String, Object> taskConditions = objectMapper.readValue(task.getProgressJson(), Map.class);

            boolean isCompleted = taskConditions.get("comment_count") != null &&
                    (Integer) taskConditions.get("comment_count") >= (Integer) ruleConditions.get("comment_count");

            if (isCompleted) {
                task.setStatus(AppConstants.COMPLETED);
                userTaskProgressRepository.save(task);
                log.info("用户{} 评论任务已完成", userId);
            }
        } catch (Exception e) {
            log.error("Error updating COMMENT task", e);
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
        return taskRule.getRewardPoints();
    }

    private String initializeProgress(Long userId) {
        return "{}"; // 评论任务默认进度为空
    }

    private String getTaskType() {
        return AppConstants.COMMENT;
    }
}
