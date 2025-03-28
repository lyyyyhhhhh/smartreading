package com.reading.ifaceutil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.service.UserReadingLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reading")
public class UserReadingLogController {
    @Autowired
    private UserReadingLogService userReadingLogService;
    @PostMapping("/submit")
    public String readingSubmit(@RequestBody Map<String, Integer> requestBody) {
        Long userId = Long.valueOf(requestBody.get("userId"));
        Long articleId = Long.valueOf(requestBody.get("articleId"));
        int addedTime = requestBody.get("addedTime");
        return userReadingLogService.readingSubmit(articleId, userId, addedTime);
    }

    @GetMapping("/total")
    public int getReadingTime(@RequestParam Long userId) {
        return userReadingLogService.getReadingTime(userId);
    }
}
