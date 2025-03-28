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

    private int similarStart = 0;
     public void updateRecommendations() {
        List<Long> frequentUserIds = recommendationService.getFrequentUserIds();
        int similarUserNums = 4;
        // 避免 similarStart 超出用户总数
        if (similarStart >= frequentUserIds.size()) {
            similarStart = 0;
        }
        int maxArticles = 30;
        Map<Long, List<Long>> recommendations = recommendationService.getRecommendedArticles(frequentUserIds, similarStart, similarUserNums, maxArticles);
        recommendationCache.updateCache(recommendations);
        log.info("推荐数据已更新: {}", recommendations);
        similarStart += similarUserNums;
    }
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateSimilarStart() {
        similarStart = 0;
    }
    @PostConstruct
    public void init() {
        updateRecommendations(); // 手动调用
    }


}
