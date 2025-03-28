package com.reading.ifaceutil.controller;

import com.reading.ifaceutil.model.dto.response.HeatResponse;
import com.reading.ifaceutil.model.dto.response.HeatResponseWithDiff;
import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.service.ArticleHeatService;
import com.reading.ifaceutil.service.UserPointsService;
import com.reading.ifaceutil.utils.constants.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/heat")
public class ArticleHeatController {
    @Autowired
    private ArticleHeatService articleHeatService;

    @Autowired
    private UserPointsService userPointsService;

    @Autowired
    private KafkaProducer kafkaProducer;
    /**
     * 根据文章 ID 获取文章热度值
     * @param articleId 文章 ID
     * @return 文章热度值
     */
    @GetMapping("/getHeat")
    public HeatResponseWithDiff getArticleHotness(@RequestParam Long articleId) {
        return articleHeatService.getArticleHeat(articleId);
    }

    @GetMapping("/getAllHeats")
    public List<HeatResponse> getShowArticleHeats() {
        return articleHeatService.getShowArticleHeats();
    }

    @GetMapping("/reload")
    public List<HeatResponse> reloadRankCache() {
        return articleHeatService.reloadRankCache();
    }

    @PostMapping("/contributeHeat")
    public Double contributeHeatByArticleId(@RequestParam Long userId, @RequestParam Long articleId, @RequestParam Double heat) {
        // 增加热度
        Double newScore = articleHeatService.incrHeatByArticleId(articleId, heat);
        // 扣减积分
        int points = (int) (-heat * AppConstants.POINTS_TO_HEAT);
        userPointsService.changePoints(userId, points);
        // 记录日志
        kafkaProducer.sendRewardPointsEvent
                (userId, AppConstants.ARTICLE_HEAT, articleId, points, "贡献热度");
        return newScore;
    }
}