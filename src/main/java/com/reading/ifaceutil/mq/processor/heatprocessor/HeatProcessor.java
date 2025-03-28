package com.reading.ifaceutil.mq.processor.heatprocessor;

import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.mq.processor.Processor;
import com.reading.ifaceutil.service.ArticleHeatService;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class HeatProcessor implements Processor {
    @Autowired
    private ArticleHeatService articleHeatService;

    @Override
    public void Distribute(Map<String, Object> message) {
        // 1. 整理message里的基本数据, 获取需要的值
        Map<String, Object> requiredData = assemble(message);
        // 没有核心数据不用消费
        if (requiredData.get("articleId") == null) {
            return;
        }
        // 2. 同步处理业务逻辑 | 异步处理业务逻辑 + 重试机制
        handleHeat(requiredData);
    }

    private Map<String, Object> assemble(Map<String, Object> message) {
        Map<String, Object> requiredData = new HashMap<>();
        String actionType = (String) message.get("actionType");
        if (AppConstants.READING.equals(actionType)) {
            requiredData.put("addedTime", message.get("addedTime"));
        }
        requiredData.put("actionType", actionType);
        requiredData.put("articleId", message.get("articleId"));
        return requiredData;
    }

    private void handleHeat(Map<String, Object> requiredData) {
        // 3. 整理拼接业务数据
        String actionType = (String) requiredData.get("actionType");
        Long articleId = Long.parseLong(requiredData.get("articleId").toString());
        Double rate = AppConstants.INTERACTIONS_TO_HEAT.get(actionType);
        double heats = 0.0;
        if (AppConstants.READING.equals(requiredData.get("actionType"))) {
            heats = ((Integer) requiredData.get("addedTime") / 10) * rate;
        } else if (AppConstants.COMMENT.equals(requiredData.get("actionType"))) {
            heats = 1 * rate;
        }
        // 4. 业务逻辑
        articleHeatService.incrHeatByArticleId(articleId, heats);
    }
}
