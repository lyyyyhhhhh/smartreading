package com.reading.ifaceutil.mq;

import com.reading.ifaceutil.model.UserPointsLog;
import com.reading.ifaceutil.repository.userpointslog.UserPointsLogRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
public class RewardPointsConsumer {
    @Autowired
    private UserPointsLogRepository userPointsLogRepository;


    // Kafka监听消费 RewardPointsTopic 消息
    @KafkaListener(topics = "reward-points", groupId = "task-service")
    @Transactional
    public void handleRewardPoints(Map<String, Object> message) {
        log.info("Kafka consumer consumeRewardPoints message {}", message);
        Long userId = Long.parseLong(message.get("userId").toString());
        Long referenceId = Long.parseLong(message.get("referenceId").toString());
        String referenceTable = message.get("referenceTable").toString();
        String desc = message.get("description").toString();
        int points = Integer.parseInt(message.get("points").toString());
        // 更新用户积分, 同步更新过了
        // updateUserPoints(userId, points);
        // 记录积分变动日志
        recordPointsLog(userId, referenceTable, referenceId, points, desc);
    }


    // 记录积分变动日志到 user_points_log 表
    private void recordPointsLog(Long userId, String referenceTable, Long referenceId, int points, String desc) {
        UserPointsLog userPointsLog = new UserPointsLog();
        userPointsLog.setUserId(userId);
        userPointsLog.setPoints(points);
        userPointsLog.setReferenceTable(referenceTable);
        userPointsLog.setReferenceId(referenceId);  // 任务ID作为参考
        userPointsLog.setCreateTime(LocalDateTime.now());
        userPointsLog.setDescription(desc);
        userPointsLogRepository.save(userPointsLog);
        log.info("用户{} 积分变动记录已保存，关联表名{}，任务ID：{}，变动积分：{}", userId, referenceTable, referenceId, points);
    }

}
