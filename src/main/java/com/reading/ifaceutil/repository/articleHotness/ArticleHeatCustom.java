package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.cache.CacheKey;
import com.reading.ifaceutil.model.dto.response.HeatResponse;

import java.util.List;

public interface ArticleHeatCustom {
    // 从redis里获取到榜单元素的kv
    Double rcGetByArticleId(CacheKey articleKey);
    // 将榜单元素缓存到redis里
    void rcSetByArticleId(CacheKey articleKey, Double articleHeat);
    // 删除元素kv
    boolean rcDelByArticleId(CacheKey articleKey);
    // 增加榜单元素的分值
    Double rcIncrByArticleId(CacheKey articleKey, Double heat);

    // 从redis里获取榜单元素的排名
    Long rcGetRankByArticleId(CacheKey heatKey, String subKey);
    // 根据排名从redis里获取榜单元素的分值
    Double rcGetScoreByRank(CacheKey heatKey, Long rank);
    // 将榜单元素批量写到zset里
    void rcZAddSortedSetMulti(CacheKey heatKey, List<HeatResponse> articleHeatList);
    // 范围获取zset里的元素
    List<HeatResponse> rcZGetSortedSet(CacheKey heatKey, Long start, Long end);
    // 删除zset的某个元素
    Long rcZRemoveSortedSet(CacheKey heatKey, String subKey);
    // 使用lua脚本增加zset某个元素的score
    void rcRankPushInSortedSetWithIncr(CacheKey heatKey, String subKey, Double score, Long limit);

    // 自定义sql
    // 从mysql里根据score降序获取所有元素
    List<ArticleHeat> getAllByScoreDesc(Long limit);
}
