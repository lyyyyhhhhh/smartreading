package com.reading.ifaceutil.service;

import com.reading.ifaceutil.model.UserPoints;
import com.reading.ifaceutil.repository.UserPointsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class UserPointsService {

    @Autowired
    private UserPointsRepository userPointsRepository;

    public UserPoints getPointsAndCoins(Long userId) {
        return userPointsRepository.findByUserId(userId)
                .orElse(new UserPoints(userId, 0, 0, LocalDateTime.now()));
    }
    public UserPoints findByUserId(Long userId) {
        return userPointsRepository.findByUserId(userId)
                .orElse(new UserPoints(userId,0, 0, LocalDateTime.now()));
    }

    public void savePoints(UserPoints userPoints) {
        userPointsRepository.save(userPoints);
    }

//    public boolean changeCoins(Long userId, int coins) {
//        // 根据用户 ID 查询用户的积分信息
//        Optional<UserPoints> userPointsOptional = userPointsRepository.findByUserId(userId);
//        UserPoints userPoints = userPointsOptional.get();
//        // 扣除相应智阅币
//        userPoints.setCoins(userPoints.getCoins() + coins);
//        // 更新最后更新时间
//        userPoints.setLastUpdateTime(LocalDateTime.now());
//        // 保存更新后的信息到数据库
//        userPointsRepository.save(userPoints);
//        return true;
//    }

    public boolean changePoints(Long userId, int points) {
        // 根据用户 ID 查询用户的积分信息
        Optional<UserPoints> userPointsOptional = userPointsRepository.findByUserId(userId);
        UserPoints userPoints = userPointsOptional.orElse(new UserPoints(userId, 0, 0, LocalDateTime.now()));
        // 扣除相应智阅币
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        log.info("积分详细{}， {}", userPoints.getTotalPoints(), points);
        // 更新最后更新时间
        userPoints.setLastUpdateTime(LocalDateTime.now());
        // 保存更新后的信息到数据库
        userPointsRepository.save(userPoints);
        return true;
    }

    public boolean exchangePoints(Long userId, Integer pointsToExchange, Integer exchangedCoin) {
        // 根据用户 ID 查询用户的积分信息
        Optional<UserPoints> userPointsOptional = userPointsRepository.findByUserId(userId);

        if (userPointsOptional.isPresent()) {
            UserPoints userPoints = userPointsOptional.get();
            // 检查用户积分是否足够
            if (userPoints.getTotalPoints() >= pointsToExchange) {
                // 扣除相应积分
                userPoints.setTotalPoints(userPoints.getTotalPoints() - pointsToExchange);
                // 增加相应的智阅币
                userPoints.setCoins(userPoints.getCoins() + exchangedCoin);
                // 更新最后更新时间
                userPoints.setLastUpdateTime(LocalDateTime.now());
                // 保存更新后的信息到数据库
                userPointsRepository.save(userPoints);
                return true;
            }
        }
        return false;
    }
}