package com.reading.ifaceutil.controller;

import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.model.UserPurchasedArticle;
import com.reading.ifaceutil.service.UserPointsService;
import com.reading.ifaceutil.service.UserPurchasedArticleService;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user-purchased-articles")
public class UserPurchasedArticleController {
    @Autowired
    private UserPurchasedArticleService userPurchasedArticleService;
    @Autowired
    private UserPointsService userPointsService;
    @Autowired
    private KafkaProducer kafkaProducer;
    // 用户购买文章记录
    @PostMapping
    public UserPurchasedArticle save(@RequestBody UserPurchasedArticle userPurchasedArticle) {
        userPointsService.changePoints(userPurchasedArticle.getUserId(), -userPurchasedArticle.getPurchasePrice());
        String desc = "购买文章：" + userPurchasedArticle.getArticleName();
        userPurchasedArticle = userPurchasedArticleService.save(userPurchasedArticle);
        kafkaProducer.sendRewardPointsEvent
                (userPurchasedArticle.getUserId(), AppConstants.USER_PURCHASED_ARTICLES, (long) userPurchasedArticle.getId(), -userPurchasedArticle.getPurchasePrice(), desc);
        return userPurchasedArticle;
    }

    // 根据用户 ID 查询用户购买的文章列表
    @GetMapping("/user")
    public List<UserPurchasedArticle> findByUserId(@RequestParam Integer userId) {
        return userPurchasedArticleService.findByUserId(userId);
    }
}