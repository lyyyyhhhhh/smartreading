package com.reading.ifaceutil.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.ifaceutil.utils.constants.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class KafkaProducer {
    private static final String UserActionTopic = "user-actions";
    private static final String RewardPointsTopic = "reward-points";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendSignInEvent(Long userId, LocalDate date) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("actionType", AppConstants.SIGN_IN);
        message.put("date", date.toString());
        kafkaTemplate.send(UserActionTopic, message);
    }

    public void sendCommentEvent(Long articleId, Long userId, String comment) {
        Map<String, Object> message = new HashMap<>();
        message.put("articleId", articleId);
        message.put("userId", userId);
        message.put("actionType", AppConstants.COMMENT);
        message.put("comment", comment);
        kafkaTemplate.send(UserActionTopic, message);
    }

    public void sendReadingEvent(Long articleId, Long userId, int addedTime) {
        Map<String, Object> message = new HashMap<>();
        message.put("articleId", articleId);
        message.put("userId", userId);
        message.put("actionType", AppConstants.READING);
        message.put("addedTime", addedTime);
        kafkaTemplate.send(UserActionTopic, message);
    }

    public void sendRewardPointsEvent(Long userId, String referenceTable, Long referenceId, int points, String description) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("referenceTable", referenceTable);
        message.put("referenceId", referenceId);
        message.put("points", points);
        message.put("description", description);
        log.info("Kafka producer sendRewardPointsEvent message  {}", message);
        kafkaTemplate.send(RewardPointsTopic, message);
    }

}
