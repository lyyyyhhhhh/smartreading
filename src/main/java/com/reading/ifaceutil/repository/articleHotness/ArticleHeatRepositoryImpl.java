package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.cache.CacheKey;
import com.reading.ifaceutil.model.dto.response.HeatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ArticleHeatRepositoryImpl implements ArticleHeatCustom {
    // 通过组合来调用 Redis 和 MySQL 的操作
    @Autowired
    private ArticleHeatCustomRedis articleHeatCustomRedis;
    @Autowired
    private ArticleHeatCustomMysql articleHeatCustomMysql;

    // Redis 操作方法
    @Override
    public Double rcGetByArticleId(CacheKey articleKey) {
        return articleHeatCustomRedis.rcGetByArticleId(articleKey);
    }

    @Override
    public void rcSetByArticleId(CacheKey articleKey, Double articleHeat) {
        articleHeatCustomRedis.rcSetByArticleId(articleKey, articleHeat);
    }

    @Override
    public boolean rcDelByArticleId(CacheKey articleKey) {
        return articleHeatCustomRedis.rcDelByArticleId(articleKey);
    }

    @Override
    public Double rcIncrByArticleId(CacheKey articleKey, Double heat) {
        return articleHeatCustomRedis.rcIncrByArticleId(articleKey, heat);
    }

    @Override
    public Long rcGetRankByArticleId(CacheKey heatKey, String subKey) {
        return articleHeatCustomRedis.rcGetRankByArticleId(heatKey, subKey);
    }

    @Override
    public Double rcGetScoreByRank(CacheKey heatKey, Long rank) {
        return articleHeatCustomRedis.rcGetScoreByRank(heatKey, rank);
    }

    @Override
    public void rcZAddSortedSetMulti(CacheKey heatKey, List<HeatResponse> articleHeatList) {
        articleHeatCustomRedis.rcZAddSortedSetMulti(heatKey, articleHeatList);
    }

    @Override
    public List<HeatResponse> rcZGetSortedSet(CacheKey heatKey, Long start, Long end) {
        return articleHeatCustomRedis.rcZGetSortedSet(heatKey, start, end);
    }

    @Override
    public Long rcZRemoveSortedSet(CacheKey heatKey, String subKey) {
        return articleHeatCustomRedis.rcZRemoveSortedSet(heatKey, subKey);
    }

    @Override
    public void rcRankPushInSortedSetWithIncr(CacheKey heatKey, String subKey, Double score, Long limit) {
        articleHeatCustomRedis.rcRankPushInSortedSetWithIncr(heatKey, subKey, score, limit);
    }

    // MySQL 操作方法
    @Override
    public List<ArticleHeat> getAllByScoreDesc(Long limit) {
        return articleHeatCustomMysql.getAllByScoreDesc(limit);
    }
}