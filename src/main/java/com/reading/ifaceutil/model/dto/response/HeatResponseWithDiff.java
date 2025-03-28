package com.reading.ifaceutil.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeatResponseWithDiff {
    // 当前排名, 如果在排行榜限定个数以外返回0
    public Long rank;
    // 当前分值
    public double heat;
    // 距离上一名的分值
    public double heatDiff;
}
