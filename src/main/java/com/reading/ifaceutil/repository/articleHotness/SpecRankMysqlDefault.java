package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecRankMysqlDefault implements ISpecRankMysql {
    @Autowired
    private ArticleHeatRepository articleHeatRepository;
    @Override
    public ArticleHeat findByArticleId(Long articleId) {
        Optional<ArticleHeat> optional = articleHeatRepository.findByArticleId(articleId);
        return optional.orElse(null);
    }

    @Override
    public List<ArticleHeat> findAllByScoreDesc(Long limit) {
        return articleHeatRepository.getAllByScoreDesc(limit);
    }

    @Override
    public int deleteByArticleId(Long articleId) {
        return articleHeatRepository.deleteArticleHeatByArticleId(articleId);
    }

    @Override
    public boolean SpecRankMysqlIncr(Long articleId, Double score, boolean newItem) {
        boolean del = false;
        Optional<ArticleHeat> optional = articleHeatRepository.findByArticleId(articleId);
        ArticleHeat articleHeat = optional.orElse(new ArticleHeat());
        if (newItem) {
            articleHeat.setArticleId(articleId);
            articleHeat.setHeat(score);
            articleHeatRepository.save(articleHeat);
            return del;
        }
        if (articleHeat.getHeat() + score > 0) {
            articleHeat.setHeat(articleHeat.getHeat() + score);
            articleHeatRepository.save(articleHeat);
            return del;
        }
        del = true;
        return del;
    }
}
