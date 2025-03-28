//package com.reading.ifaceutil.utils.algorithm;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class Recommendation {
//    public static double CosineSimilarity(Map<Long, Double> userA, Map<Long, Double> userB) {
//        Set<Long> commonArticles = new HashSet<>(userA.keySet());
//        commonArticles.retainAll(userB.keySet());
//
//        if (commonArticles.isEmpty()) {
//            return 0.0;
//        }
//
//        double dotProduct = 0.0;
//        double magnitudeA = 0.0;
//        double magnitudeB = 0.0;
//
//        for (Long articleId : commonArticles) {
//            dotProduct += userA.get(articleId) * userB.get(articleId);
//        }
//
//        for (Double score : userA.values()) {
//            magnitudeA += Math.pow(score, 2);
//        }
//
//        for (Double score : userB.values()) {
//            magnitudeB += Math.pow(score, 2);
//        }
//
//        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
//    }
//    public static List<Long> FindSimilarUsers(Long targetUserId, Map<Long, Map<Long, Double>> userArticleMatrix, int k) {
//        Map<Long, Double> targetUserMatrix = userArticleMatrix.get(targetUserId);
//        List<Map.Entry<Long, Double>> similarityList = new ArrayList<>();
//
//        // 计算目标用户与其他用户的相似度
//        for (Map.Entry<Long, Map<Long, Double>> entry : userArticleMatrix.entrySet()) {
//            Long userId = entry.getKey();
//            if (!userId.equals(targetUserId)) {
//                double similarity = CosineSimilarity(targetUserMatrix, entry.getValue());
//                similarityList.add(new AbstractMap.SimpleEntry<>(userId, similarity));
//            }
//        }
//
//        // 根据相似度降序排序并取前 k 个相似用户
//        similarityList.sort((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()));
//
//        List<Long> similarUsers = new ArrayList<>();
//        for (int i = 0; i < k && i < similarityList.size(); i++) {
//            similarUsers.add(similarityList.get(i).getKey());
//        }
//
//        return similarUsers;
//    }
//
//    public static List<Long> RecommendArticles(Long targetUserId, Map<Long, Map<Long, Double>> userArticleMatrix, List<Long> similarUsers) {
//        Map<Long, Double> targetUserMatrix = userArticleMatrix.get(targetUserId);
//        Map<Long, Double> articleScores = new HashMap<>();
//
//        // 对于每个相似用户，根据其评分为目标用户推荐文章
//        for (Long similarUserId : similarUsers) {
//            Map<Long, Double> similarUserMatrix = userArticleMatrix.get(similarUserId);
//
//            for (Map.Entry<Long, Double> entry : similarUserMatrix.entrySet()) {
//                Long articleId = entry.getKey();
//                Double score = entry.getValue();
//
//                // 如果目标用户没有评分该文章，则推荐
//                if (!targetUserMatrix.containsKey(articleId)) {
//                    articleScores.put(articleId, articleScores.getOrDefault(articleId, 0.0) + score);
//                }
//            }
//        }
//
//        // 根据综合评分排序，并返回最推荐的文章
//        return articleScores.entrySet().stream()
//                .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toList());
//    }
//}
