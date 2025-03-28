package com.reading.ifaceutil.repository.highlight;

import java.util.List;
import com.reading.ifaceutil.model.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Highlight h WHERE h.userId = :userId AND h.articleId = :articleId AND h.highlightId = :highlightId")
    void deleteByUserIdAndArticleIdAndHighlightId(Long userId, Long articleId, Long highlightId);

    @Query("SELECT h FROM Highlight h WHERE h.userId = :userId AND (:articleId IS NULL OR h.articleId = :articleId)")
    List<Highlight> selectUserHighlights(@Param("userId") Long userId, @Param("articleId") Long articleId);

    @Query("SELECT h.articleId, COUNT(h.id) FROM Highlight h GROUP BY h.articleId")
    List<Object[]> getArticleCommentCounts();

    // 根据 userId 查询该用户所有的高亮记录
    List<Highlight> findByUserId(Long userId);
}