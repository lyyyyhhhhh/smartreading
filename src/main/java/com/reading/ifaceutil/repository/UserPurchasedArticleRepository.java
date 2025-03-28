package com.reading.ifaceutil.repository;

import com.reading.ifaceutil.model.UserPurchasedArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPurchasedArticleRepository extends JpaRepository<UserPurchasedArticle, Integer> {

    // 根据用户 ID 查询用户购买的文章列表
    List<UserPurchasedArticle> findByUserId(Long userId);
}
