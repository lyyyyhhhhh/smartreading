package com.reading.ifaceutil.controller;

import com.reading.ifaceutil.cache.RecommendationCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class RecommendController {
    @Autowired
    private RecommendationCache recommendationCache;

    @GetMapping("/recommend")
    public List<Long> getRecommendedArticles(@RequestParam Long userId) {
        return recommendationCache.getRecommendedArticles(userId);
    }
}
