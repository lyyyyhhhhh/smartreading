package com.reading.ifaceutil.mq.processor.taskprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public abstract class AbstractTaskProcessor implements TaskBaseProcessor {
    @Autowired
    protected ObjectMapper objectMapper; // Jackson ObjectMapper
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;

    // 公有部分抽象到abstract中完成
    public UserTaskProgress createCommonNewTask(Long userId, String taskType, String progressJson) {
        UserTaskProgress newTask = new UserTaskProgress();
        newTask.setUserId(userId);
        newTask.setTaskType(taskType);
        newTask.setStatus(AppConstants.PENDING);
        newTask.setCreateDate(LocalDate.now());
        newTask.setProgressJson(progressJson);
        userTaskProgressRepository.save(newTask);
        return newTask;
    }
}
