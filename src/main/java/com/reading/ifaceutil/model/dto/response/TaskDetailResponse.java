package com.reading.ifaceutil.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDetailResponse {
    private Integer claimedNum;
    private Integer completedNum;
    // 任务整体进度
    private String status;
    // 任务当前次进度
    private String progressJson;
}
