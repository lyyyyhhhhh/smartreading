package com.reading.ifaceutil.repository.highlight;

import com.reading.ifaceutil.model.UserSignIn;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HighlightRepositoryImpl implements HighlightRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Object[]> getArticleCommentCounts() {
        String jpql = "SELECT h.articleId, COUNT(h.id) FROM Highlight h GROUP BY h.articleId";
        return entityManager.createQuery(jpql).getResultList();
    }

}
