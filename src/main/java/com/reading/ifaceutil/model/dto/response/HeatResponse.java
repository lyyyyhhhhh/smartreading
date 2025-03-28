package com.reading.ifaceutil.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeatResponse {
    // 文章id
    private Long articleId;
    // 当前分值
    private Double heat;
}
