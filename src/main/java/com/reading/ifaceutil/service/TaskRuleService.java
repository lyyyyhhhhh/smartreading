package com.reading.ifaceutil.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.mq.processor.taskprocessor.TaskBaseProcessor;
import com.reading.ifaceutil.repository.TaskRulesRepository;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TaskRuleService {
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper
    @Autowired
    @Lazy
    private Map<String, TaskBaseProcessor> taskProcessors; // 自动注入所有 TaskProcessor
    public List<TaskRules> getTaskRules() {
        return taskRulesRepository.findAll();
    }
    public TaskRules findByTaskType(String taskType) {
        return taskRulesRepository.findByTaskType(taskType);
    }

    public int calculatePoints(UserTaskProgress task) {
        TaskBaseProcessor processor = taskProcessors.get((AppConstants.taskTypeToProcessorMap.get(task.getTaskType())));
        return processor.calculatePoints(task);
    }
}
