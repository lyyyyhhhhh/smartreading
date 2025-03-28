package com.reading.ifaceutil.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reading.ifaceutil.mq.KafkaProducer;
import com.reading.ifaceutil.model.UserSignIn;
import com.reading.ifaceutil.repository.usersignin.UserSignInRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserSignInService {
    @Autowired
    private UserSignInRepository userSignInRepository;
    @Autowired
    private KafkaProducer kafkaProducer;
    @Transactional
    public String signIn(Long userId) throws JsonProcessingException {
        LocalDate today = LocalDate.now();
        Optional<UserSignIn> existingSignIn = userSignInRepository.findByUserIdAndDate(userId, today);
        if (existingSignIn.isPresent()) {
            return "今天已经签到过了";
        }
        UserSignIn newSignIn = new UserSignIn();
        newSignIn.setUserId(userId);
        newSignIn.setDate(today);
        userSignInRepository.save(newSignIn);
        kafkaProducer.sendSignInEvent(userId, today);
        return "签到成功";
    }

}
