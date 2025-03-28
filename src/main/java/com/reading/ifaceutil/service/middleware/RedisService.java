package com.reading.ifaceutil.service.middleware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void zSetAdd(String key, Object item, double score) {
        redisTemplate.opsForZSet().add(key, item, score);
    }

    public Long getRank(String key, Object item) {
        return redisTemplate.opsForZSet().rank(key, item);
    }
}
