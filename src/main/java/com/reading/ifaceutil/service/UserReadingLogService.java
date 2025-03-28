package com.reading.ifaceutil.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reading.ifaceutil.model.UserReadingLog;
import com.reading.ifaceutil.model.UserSignIn;
import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.repository.UserReadingLogRepository;
import com.reading.ifaceutil.repository.usersignin.UserSignInRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserReadingLogService {
    @Autowired
    private UserReadingLogRepository userReadingLogRepository;
    @Autowired
    private KafkaProducer kafkaProducer;

    @Transactional
    public String readingSubmit(Long articleId, Long userId, int addedTime) {
        UserReadingLog readingLog = userReadingLogRepository.findByUserIdAndDate(userId, LocalDate.now())
                .map(log -> {
                    // 如果记录存在，更新
                    log.setTotalSecond(log.getTotalSecond() + addedTime);
                    return log;
                })
                .orElseGet(() -> {
                    // 如果记录不存在，创建新的
                    UserReadingLog newLog = new UserReadingLog();
                    newLog.setUserId(userId);
                    newLog.setTotalSecond(addedTime);  // 新建时使用 addedTime
                    newLog.setDate(LocalDate.now());
                    return newLog;
                });
        userReadingLogRepository.save(readingLog);
        kafkaProducer.sendReadingEvent(articleId, userId, addedTime);
        return "更新阅读时间成功";
    }

    public int getReadingTime(Long userId) {
        Optional<UserReadingLog> readingLog = userReadingLogRepository.findByUserIdAndDate(userId, LocalDate.now());
        return readingLog.map(UserReadingLog::getTotalSecond).orElse(0);
    }
}