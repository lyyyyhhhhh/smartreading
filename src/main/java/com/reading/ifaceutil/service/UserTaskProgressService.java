package com.reading.ifaceutil.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.mq.processor.taskprocessor.TaskBaseProcessor;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class UserTaskProgressService {
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;
    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper

    @Autowired
    @Lazy
    private Map<String, TaskBaseProcessor> taskProcessors;

    public List<UserTaskProgress> getByIdDateTypeStatDesc(Long userId, LocalDate createDate, String taskType, String status) {
        return userTaskProgressRepository.findByUserIdAndCreateDateAndTaskTypeAndStatusOrderByCreateTimeDesc(userId, createDate, taskType, status)
                .stream().max(Comparator.comparing(UserTaskProgress::getId)).stream().toList();
    }

    public List<UserTaskProgress> getSpecifiedTasks(Long userId, LocalDate createDate, String taskType) {
        return userTaskProgressRepository.findByUserIdAndCreateDateAndTaskType(userId, createDate, taskType);
    }

    public UserTaskProgress createNewTask(Long userId, String taskType) {
        TaskBaseProcessor processor = taskProcessors.get(AppConstants.taskTypeToProcessorMap.get(taskType));
        if (processor == null) {
            throw new IllegalArgumentException("未知任务类型: " + taskType);
        }
        return processor.createNewTask(userId);
    }
}
