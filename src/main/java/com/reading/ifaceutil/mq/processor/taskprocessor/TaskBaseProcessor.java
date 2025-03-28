package com.reading.ifaceutil.mq.processor.taskprocessor;

import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.mq.processor.Processor;

import java.util.Map;

public interface TaskBaseProcessor {
    void assemble(UserTaskProgress task, Map<String, Object> requiredData);
    Map<String, Object> updateTask(UserTaskProgress task, TaskRules taskRule, Long userId);
    UserTaskProgress createNewTask(Long userId);
    int calculatePoints(UserTaskProgress task);
}
