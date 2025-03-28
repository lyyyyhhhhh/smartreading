package com.reading.ifaceutil.task;

import com.reading.ifaceutil.model.TaskRules;
import com.reading.ifaceutil.model.UserTaskProgress;
import com.reading.ifaceutil.service.TaskRuleService;
import com.reading.ifaceutil.service.UserTaskProgressService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TaskProgressScheduler {
    @Autowired
    private UserTaskProgressService userTaskProgressService;
    @Autowired
    private TaskRuleService taskRulesService;
    @Scheduled(cron = "0 0 0 * * ?")
    public void createDailyTasks() {
        log.info("[Scheduled] createDailyTasks()");
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L);
        List<TaskRules> allRules = taskRulesService.getTaskRules();
        LocalDate currentDate = LocalDate.now();
        for (TaskRules rule : allRules) {
            for (Long userId : userIds) {
                List<UserTaskProgress> userTaskProgress = userTaskProgressService.getSpecifiedTasks(userId, currentDate, rule.getTaskType());
                if (!userTaskProgress.isEmpty()) {
                    continue;
                }
                userTaskProgressService.createNewTask(userId, rule.getTaskType());
            }
        }
    }
    @PostConstruct
    public void init() {
        createDailyTasks(); // 手动调用
    }
}
