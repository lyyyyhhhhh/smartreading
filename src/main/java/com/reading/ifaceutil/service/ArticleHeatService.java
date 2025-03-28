package com.reading.ifaceutil.service;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.cache.ArticleHeatCacheKey;
import com.reading.ifaceutil.model.cache.CacheKey;
import com.reading.ifaceutil.model.dto.response.HeatResponse;
import com.reading.ifaceutil.model.dto.response.HeatResponseWithDiff;
import com.reading.ifaceutil.repository.articleHotness.ArticleHeatRepository;
import com.reading.ifaceutil.repository.articleHotness.ISpecRankMysql;
import com.reading.ifaceutil.repository.articleHotness.SpecRankMysqlFactory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ArticleHeatService {
    @Autowired
    private ArticleHeatRepository articleHeatRepository;

    @Autowired
    private SpecRankMysqlFactory specRankMysqlFactory;

    // 为节省性能, 榜单排行榜最大容量
    public static final Long ZSET_LIMIT = 10L;

    // 返回某篇文章热度值 和 排行(前)
    public HeatResponseWithDiff getArticleHeat(Long articleId) {
        HeatResponseWithDiff heatResponseWithDiff = new HeatResponseWithDiff();
        // 查redis获取当前分值
        CacheKey articleKey = ArticleHeatCacheKey.RcGetArticleKey(articleId);
        Double articleHeat = articleHeatRepository.rcGetByArticleId(articleKey);
        if (articleHeat != null) {
            heatResponseWithDiff.heat = articleHeat;
        } else {
            // 查db获取当前分值
            ISpecRankMysql rankMysql = specRankMysqlFactory.getInstance();
            ArticleHeat item = rankMysql.findByArticleId(articleId);
            if (item != null) {
                articleHeat = item.getHeat();
                articleHeatRepository.rcSetByArticleId(articleKey, articleHeat);
                heatResponseWithDiff.heat = articleHeat;
            } else {
                return null;
            }
        }
        // 查询在榜单上的排名
        CacheKey heatKey = ArticleHeatCacheKey.RcGetHeatKey();
        Long rank = articleHeatRepository.rcGetRankByArticleId(heatKey, articleId.toString());
        // 如果rank为null说明没上榜, 因此返回的diff是与榜上最后一名的分值之差
        long lastRank = ZSET_LIMIT - 1;
        if (rank != null) {
            // 如果排行第一, 则显示超过第二xxx分
            if (rank == 0) {
                lastRank = 1;
            } else {
                lastRank = rank - 1;
            }
        }
        heatResponseWithDiff.rank = rank;
        heatResponseWithDiff.heatDiff = articleHeatRepository.rcGetScoreByRank(heatKey, lastRank) - articleHeat;
        // 保留 1 位小数
        BigDecimal bd = new BigDecimal(heatResponseWithDiff.heatDiff).setScale(1, RoundingMode.HALF_UP);
        heatResponseWithDiff.heatDiff = bd.doubleValue();
        return heatResponseWithDiff;
    }

    // 强制将数据刷到zset缓存
    public List<HeatResponse> reloadRankCache() {
        List<HeatResponse> heatResponseList = new ArrayList<>();
        CacheKey heatKey = ArticleHeatCacheKey.RcGetHeatKey();
        // 可加分布式锁防大量回源
        ISpecRankMysql rankMysql = specRankMysqlFactory.getInstance();
        List<ArticleHeat> items = rankMysql.findAllByScoreDesc(ZSET_LIMIT);
        for (ArticleHeat item : items) {
            HeatResponse heatResponse = new HeatResponse();
            heatResponse.setHeat(item.getHeat());
            heatResponse.setArticleId(item.getArticleId());
            heatResponseList.add(heatResponse);
        }
        articleHeatRepository.rcZAddSortedSetMulti(heatKey, heatResponseList);
        return heatResponseList;
    }

    public List<HeatResponse> getShowArticleHeats() {
        CacheKey heatKey = ArticleHeatCacheKey.RcGetHeatKey();
        List<HeatResponse> heatResponseList = new ArrayList<>();
        heatResponseList = articleHeatRepository.rcZGetSortedSet(heatKey, 0L, ZSET_LIMIT - 1);
        if (heatResponseList.isEmpty()) {
            return reloadRankCache();
        }
        return heatResponseList;
    }

    // 变动分数
    public Double incrHeatByArticleId(Long articleId, Double heat) {
        boolean newItem = false;
        // 1. 查看现有积分, redis -> mysql -> redis
        // 2. 如果不存在, 则判断为newItem
        HeatResponseWithDiff heatResponseWithDiff = getArticleHeat(articleId);
        if (heatResponseWithDiff == null) {
            newItem = true;
        }
        // 3. 得出积分后累加, 先更新db(新增或更新) -> 再更新redis
        CacheKey articleKey = ArticleHeatCacheKey.RcGetArticleKey(articleId);
        ISpecRankMysql rankMysql = specRankMysqlFactory.getInstance();
        boolean del = rankMysql.SpecRankMysqlIncr(articleId, heat, newItem);
        // 判断是否需要删除数据
        if (del) {
            delArticleHeat(articleId);
            return 0.0;
        }
        Double newScore = articleHeatRepository.rcIncrByArticleId(articleKey, heat);
        // 4. 使用lua脚本更新zset, 更新item, 控制zset长度
        CacheKey heatKey = ArticleHeatCacheKey.RcGetHeatKey();
        articleHeatRepository.rcRankPushInSortedSetWithIncr(heatKey, articleId.toString(), newScore, ZSET_LIMIT);
        return newScore;
    }

    // 删除元素, 包括kv, mysql, zset
    public boolean delArticleHeat(Long articleId) {
        // 1. 删除db
        ISpecRankMysql rankMysql = specRankMysqlFactory.getInstance();
        int success = rankMysql.deleteByArticleId(articleId);
        if (success != 1) {
            return false;
        }
        // 2. 删除redis中的kv
        CacheKey articleKey = ArticleHeatCacheKey.RcGetArticleKey(articleId);
        articleHeatRepository.rcDelByArticleId(articleKey);
        // 3. 删除zset中的此元素
        CacheKey heatKey = ArticleHeatCacheKey.RcGetHeatKey();
        articleHeatRepository.rcZRemoveSortedSet(heatKey, articleId.toString());
        return true;
    }
}
