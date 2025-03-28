package com.reading.ifaceutil.cache;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

@Component
public class RecommendationCache {
    private final Map<Long, List<Long>> recommendedArticlesCache = new ConcurrentHashMap<>();
    public List<Long> getRecommendedArticles(Long userId) {
        return recommendedArticlesCache.getOrDefault(userId, Collections.emptyList());
    }
    public void updateCache(Map<Long, List<Long>> newRecommendations) {
        recommendedArticlesCache.clear();
        recommendedArticlesCache.putAll(newRecommendations);
    }
}

