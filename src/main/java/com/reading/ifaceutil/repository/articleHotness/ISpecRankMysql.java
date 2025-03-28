package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;

import java.util.List;

// 封装多个mysql操作逻辑
public interface ISpecRankMysql {
    ArticleHeat findByArticleId(Long articleId);
    List<ArticleHeat> findAllByScoreDesc(Long limit);
    int deleteByArticleId(Long articleId);
    // newScore可能为负值, 为负值返回删除
    boolean SpecRankMysqlIncr(Long articleId, Double newScore, boolean newItem);
}
