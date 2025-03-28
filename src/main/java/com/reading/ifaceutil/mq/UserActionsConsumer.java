package com.reading.ifaceutil.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reading.ifaceutil.mq.processor.Processor;
import com.reading.ifaceutil.repository.TaskRulesRepository;
import com.reading.ifaceutil.repository.UserTaskProgressRepository;
import com.reading.ifaceutil.service.UserTaskProgressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class UserActionsConsumer {
    @Autowired
    private UserTaskProgressRepository userTaskProgressRepository;
    @Autowired
    private UserTaskProgressService userTaskProgressService;
    @Autowired
    private TaskRulesRepository taskRulesRepository;
    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper
    @Autowired
    private Map<String, Processor> commonProcessors; // 自动注入所有 commonProcessor

    private final ExecutorService customExecutor = Executors.newFixedThreadPool(10);


    @KafkaListener(topics = "user-actions", groupId = "common-service")
    public void handleUserAction(Map<String, Object> message) {
        log.info("Kafka consumer handleUserAction message {}", message);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // 一个topic通过多个processor进行处理, 而不是一个topic创建多个消费者组
        for (Processor processor : commonProcessors.values()) {
            // 多线程处理, 确保都分发后(业务异步处理)提交offset
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> distribute(processor, message), customExecutor);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
    }

    private void distribute(Processor processor, Map<String, Object> message) {
        try {
            processor.Distribute(message);
        } catch (Exception e) {
            log.error("message {} distribute to {} error", message, processor, e);
        }
    }
}
