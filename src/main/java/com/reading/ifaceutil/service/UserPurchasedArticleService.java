package com.reading.ifaceutil.service;

import com.reading.ifaceutil.model.UserPurchasedArticle;
import com.reading.ifaceutil.repository.UserPurchasedArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserPurchasedArticleService {

    @Autowired
    private UserPurchasedArticleRepository userPurchasedArticleRepository;

    // 保存用户购买文章记录
    public UserPurchasedArticle save(UserPurchasedArticle userPurchasedArticle) {
        return userPurchasedArticleRepository.save(userPurchasedArticle);
    }

    // 根据用户 ID 查询用户购买的文章列表
    public List<UserPurchasedArticle> findByUserId(Integer userId) {
        return userPurchasedArticleRepository.findByUserId(Long.valueOf(userId));
    }
}