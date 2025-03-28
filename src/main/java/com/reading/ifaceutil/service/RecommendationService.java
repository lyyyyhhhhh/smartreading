package com.reading.ifaceutil.service;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.Highlight;
import com.reading.ifaceutil.repository.articleHotness.ArticleHeatRepository;
import com.reading.ifaceutil.repository.highlight.HighlightRepository;
import com.reading.ifaceutil.repository.usersignin.UserSignInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserSignInRepository userSignInRepository;

    @Autowired
    private HighlightRepository highlightRepository;

    @Autowired
    private ArticleHeatRepository articleHotnessRepository;

    public List<Long> getFrequentUserIds() {
        return List.of(1L, 2L, 3L, 4L, 5L);
    }

    public Map<Long, List<Long>> getRecommendedArticles(List<Long> frequentUserIds, int similarStart, int k, int maxArticles) {
        Map<Long, Map<Long, Double>> userArticleMatrix = buildUserArticleMatrix(frequentUserIds);
        Map<Long, Double> articleHotnessMap = getArticleHotnessMap(); // 获取文章热度榜
        Map<Long, List<Long>> allUsersRecommendedArticles = new HashMap<>();

        for (Long userId : frequentUserIds) {
            List<Map.Entry<Long, Double>> similarUsers = findSimilarUsers(userId, userArticleMatrix, similarStart, k);
            List<Long> recommendedArticles = recommendArticles(userId, userArticleMatrix, similarUsers, maxArticles, articleHotnessMap);

            allUsersRecommendedArticles.put(userId, recommendedArticles); // **存入有序列表**
        }

        return allUsersRecommendedArticles;
    }

    private Map<Long, Map<Long, Double>> buildUserArticleMatrix(List<Long> frequentUserIds) {
        Map<Long, Map<Long, Double>> userArticleMatrix = new HashMap<>();

        // 获取所有文章的热度值
        List<ArticleHeat> articleHeatList = articleHotnessRepository.findAll();
        Map<Long, Double> articleHotnessMap = new HashMap<>();
        for (ArticleHeat articleHeat : articleHeatList) {
            articleHotnessMap.put(articleHeat.getArticleId(), articleHeat.getHeat());
        }

        // 为每个用户构建评分矩阵
        for (Long userId : frequentUserIds) {
            Map<Long, Double> articleScores = new HashMap<>();

            // 获取该用户的评论记录，并统计每篇文章的评论次数
            List<Highlight> userHighlights = highlightRepository.findByUserId(userId);
            Map<Long, Long> userCommentCountMap = new HashMap<>();
            for (Highlight highlight : userHighlights) {
                Long articleId = highlight.getArticleId();
                userCommentCountMap.put(articleId, userCommentCountMap.getOrDefault(articleId, 0L) + 1);
            }

            // 计算该用户的文章评分
            for (Map.Entry<Long, Long> entry : userCommentCountMap.entrySet()) {
                Long articleId = entry.getKey();
                Long userCommentCount = entry.getValue();
                Double hotness = articleHotnessMap.getOrDefault(articleId, 0.0);

                // 综合评分 = 用户的评论次数 * 2 + 文章热度值
                Double score = userCommentCount * 2.0 + hotness * 0.001;
                articleScores.put(articleId, score);
            }

            userArticleMatrix.put(userId, articleScores);
        }

        return userArticleMatrix;
    }

    private double cosineSimilarity(Map<Long, Double> userA, Map<Long, Double> userB) {
        Set<Long> commonArticles = new HashSet<>(userA.keySet());
        commonArticles.retainAll(userB.keySet());

        if (commonArticles.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double magnitudeA = 0.0;
        double magnitudeB = 0.0;

        for (Long articleId : commonArticles) {
            dotProduct += userA.get(articleId) * userB.get(articleId);
        }

        for (Double score : userA.values()) {
            magnitudeA += Math.pow(score, 2);
        }

        for (Double score : userB.values()) {
            magnitudeB += Math.pow(score, 2);
        }

        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }
    private List<Map.Entry<Long, Double>> findSimilarUsers(Long targetUserId, Map<Long, Map<Long, Double>> userArticleMatrix, int similarStart, int k) {
        Map<Long, Double> targetUserMatrix = userArticleMatrix.get(targetUserId);
        List<Map.Entry<Long, Double>> similarityList = new ArrayList<>();

        // 计算目标用户与其他用户的相似度
        for (Map.Entry<Long, Map<Long, Double>> entry : userArticleMatrix.entrySet()) {
            Long userId = entry.getKey();
            if (!userId.equals(targetUserId)) {
                double similarity = cosineSimilarity(targetUserMatrix, entry.getValue());
                if (similarity > 0) { // 只保留正相似度的用户
                    similarityList.add(new AbstractMap.SimpleEntry<>(userId, similarity));
                }
            }
        }

        // 根据相似度降序排序并取前 k 个相似用户
        similarityList.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));

        // 只返回用户 ID，并保留相似度信息
        return similarityList.stream().skip(similarStart).limit(k).collect(Collectors.toList());
    }


    private List<Long> recommendArticles(Long targetUserId, Map<Long, Map<Long, Double>> userArticleMatrix,
                                         List<Map.Entry<Long, Double>> similarUsers, int maxArticles,
                                         Map<Long, Double> articleHotnessMap) {
        Map<Long, Double> targetUserMatrix = userArticleMatrix.getOrDefault(targetUserId, new HashMap<>());
        Map<Long, Double> articleScores = new HashMap<>();

        // 计算文章推荐分数
        for (Map.Entry<Long, Double> entry : similarUsers) {
            Long similarUserId = entry.getKey();
            Map<Long, Double> similarUserMatrix = userArticleMatrix.getOrDefault(similarUserId, new HashMap<>());

            for (Map.Entry<Long, Double> articleEntry : similarUserMatrix.entrySet()) {
                Long articleId = articleEntry.getKey();
                Double score = articleEntry.getValue();
                articleScores.put(articleId, articleScores.getOrDefault(articleId, 0.0) + score);
            }
        }

        // 按评分降序排序
        List<Long> finalRecommendations = articleScores.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // 按分数降序
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // 若推荐文章不足，则补充热度榜文章
        if (finalRecommendations.size() < maxArticles) {
            List<Long> hotArticles = getTopHotArticles(articleHotnessMap, maxArticles - finalRecommendations.size(), finalRecommendations);
            finalRecommendations.addAll(hotArticles); // **保持顺序，追加**
        }

        return finalRecommendations; // **返回 List 而不是 Set**
    }

    /**
     * 获取所有文章的热度值，并按降序排序
     */
    private Map<Long, Double> getArticleHotnessMap() {
        List<ArticleHeat> articleHeatList = articleHotnessRepository.findAll();
        Map<Long, Double> articleHotnessMap = new HashMap<>();
        for (ArticleHeat articleHeat : articleHeatList) {
            articleHotnessMap.put(articleHeat.getArticleId(), articleHeat.getHeat());
        }
        return articleHotnessMap;
    }

    /**
     * 获取热度最高的文章，排除已推荐的文章
     */
    private List<Long> getTopHotArticles(Map<Long, Double> articleHotnessMap, int limit, List<Long> existingArticles) {
        return articleHotnessMap.entrySet().stream()
                .filter(entry -> !existingArticles.contains(entry.getKey())) // **去重**
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // **按热度降序**
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

}
