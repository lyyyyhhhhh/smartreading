package com.reading.ifaceutil.task;

import com.reading.ifaceutil.cache.RecommendationCache;
import com.reading.ifaceutil.service.RecommendationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RecommendScheduler {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationCache recommendationCache;

    private Long similarStart = 0L;
    private final Long similarUserNums = 4L;

    public void updateRecommendations() {
        List<Long> frequentUserIds = recommendationService.getFrequentUserIds();
        if (frequentUserIds == null || frequentUserIds.isEmpty()) {
            log.warn("未找到活跃用户，推荐更新跳过");
            return;
        }

        if (similarStart >= frequentUserIds.size()) {
            similarStart = 0L;
        }

        int maxArticles = 20;
        log.info("开始更新推荐数据: similarStart={}, similarUserNums={}, 总用户数={}",
                similarStart, similarUserNums, frequentUserIds.size());

        Map<Long, List<Long>> recommendations = recommendationService.getRecommendedArticles(
                frequentUserIds, similarStart, similarUserNums, maxArticles);

        recommendationCache.updateCache(recommendations);
        log.info("推荐数据已更新: {}", recommendations);

        similarStart += similarUserNums;
    }

    @Scheduled(cron = "0 0 */1 * * ?")  // 每 1 小时更新一次
    public void rotateSimilarStart() {
        List<Long> frequentUserIds = recommendationService.getFrequentUserIds();
        if (frequentUserIds == null || frequentUserIds.isEmpty()) {
            log.warn("无法更新 similarStart，活跃用户列表为空");
            return;
        }

        similarStart = (similarStart + similarUserNums) % frequentUserIds.size();
        log.info("similarStart 更新为: {}", similarStart);
    }

    @PostConstruct
    public void init() {
        log.info("应用启动时初始化推荐数据...");
        updateRecommendations();
    }
}