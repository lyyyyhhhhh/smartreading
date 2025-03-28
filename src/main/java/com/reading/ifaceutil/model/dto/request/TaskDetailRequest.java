package com.reading.ifaceutil.model.dto.request;

import lombok.Data;

@Data
public class TaskDetailRequest {
    private Long userId;
    private String taskType;
}
