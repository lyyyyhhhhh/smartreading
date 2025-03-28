package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.cache.CacheKey;
import com.reading.ifaceutil.model.dto.response.HeatResponse;
import com.reading.ifaceutil.utils.constants.AppConstants;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// mysql普通逻辑
@Repository
public class ArticleHeatCustomMysql implements ArticleHeatCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ArticleHeat> getAllByScoreDesc(Long limit) {
        String sql = "SELECT * FROM " + AppConstants.ARTICLE_HEAT + " ORDER BY `heat` desc limit " + limit;
        Query query = entityManager.createNativeQuery(sql, ArticleHeat.class);
        return query.getResultList();
    }

    @Override
    public Double rcGetByArticleId(CacheKey articleKey) {
        return 0.0;
    }

    @Override
    public void rcSetByArticleId(CacheKey articleKey, Double articleHeat) {

    }

    @Override
    public boolean rcDelByArticleId(CacheKey articleKey) {
        return false;
    }

    @Override
    public Double rcIncrByArticleId(CacheKey articleKey, Double heat) {
        return 0.0;
    }

    @Override
    public Long rcGetRankByArticleId(CacheKey heatKey, String subKey) {
        return 0L;
    }

    @Override
    public Double rcGetScoreByRank(CacheKey heatKey, Long rank) {
        return 0.0;
    }

    @Override
    public void rcZAddSortedSetMulti(CacheKey heatKey, List<HeatResponse> articleHeatList) {

    }

    @Override
    public List<HeatResponse> rcZGetSortedSet(CacheKey heatKey, Long start, Long end) {
        return null;
    }

    @Override
    public Long rcZRemoveSortedSet(CacheKey heatKey, String subKey) {
        return 0L;
    }

    @Override
    public void rcRankPushInSortedSetWithIncr(CacheKey heatKey, String subKey, Double score, Long limit) {

    }
}
