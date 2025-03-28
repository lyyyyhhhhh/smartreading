package com.reading.ifaceutil.controller;

import com.reading.ifaceutil.model.UserPoints;
import com.reading.ifaceutil.model.UserPointsLog;
import com.reading.ifaceutil.repository.userpointslog.UserPointsLogRepository;
import com.reading.ifaceutil.service.RecommendationService;
import com.reading.ifaceutil.service.UserPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/points")
public class UserPointsController {
    @Autowired
    private UserPointsService userPointsService;
    @Autowired
    private UserPointsLogRepository userPointsLogRepository;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/user")
    public UserPoints getPointsAndCoins(@RequestParam Long userId) {
        return userPointsService.getPointsAndCoins(userId);
    }

    @PostMapping("/exchange")
    public ResponseEntity<Map<String, Object>> exchangePoints(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Integer pointsToExchange = Integer.valueOf(request.get("points").toString());
        Integer exchangedCoin = Integer.valueOf(request.get("coins").toString());

        boolean success = userPointsService.exchangePoints(userId, pointsToExchange, exchangedCoin);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        if (success) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/log")
    public List<UserPointsLog> getPointsLogs(@RequestParam Long userId) {
         return userPointsLogRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
}
