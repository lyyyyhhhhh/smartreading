package com.reading.ifaceutil.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.service.TaskRuleService;
import com.reading.ifaceutil.service.UserSignInService;
import com.reading.ifaceutil.service.UserTaskProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sign-in")
public class UserSignInController {
    @Autowired
    private UserSignInService userSignInService;
    // temp
    @Autowired
    private UserTaskProgressService userTaskProgressService;
    @Autowired
    private TaskRuleService taskRulesService;

    @PostMapping
    public String signIn(@RequestBody Map<String, Long> requestBody) throws JsonProcessingException {
        Long userId = requestBody.get("userId");
        // temp fix逻辑
        List<TaskRules> allRules = taskRulesService.getTaskRules();
        LocalDate currentDate = LocalDate.now();
        for (TaskRules rule : allRules) {
            List<UserTaskProgress> userTaskProgress = userTaskProgressService.getSpecifiedTasks(userId, currentDate, rule.getTaskType());
            if (!userTaskProgress.isEmpty()) {
                continue;
            }
            userTaskProgressService.createNewTask(userId, rule.getTaskType());
        }
        return userSignInService.signIn(userId);
    }
}
