package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// jpa原生mysql操作
@Repository
public interface ArticleHeatRepository extends JpaRepository<ArticleHeat, Long>, ArticleHeatCustom {
    Optional<ArticleHeat> findByArticleId(Long articleId);
    int deleteArticleHeatByArticleId(Long articleId);
}
