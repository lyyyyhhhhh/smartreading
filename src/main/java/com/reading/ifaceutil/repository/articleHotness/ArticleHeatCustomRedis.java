package com.reading.ifaceutil.repository.articleHotness;

import com.reading.ifaceutil.model.ArticleHeat;
import com.reading.ifaceutil.model.cache.CacheKey;
import com.reading.ifaceutil.model.dto.response.HeatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ArticleHeatCustomRedis implements ArticleHeatCustom {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Double rcGetByArticleId(CacheKey articleKey) {
        Object ret = redisTemplate.opsForValue().get(articleKey.redisKey);
        if (ret instanceof Double) {
            return (Double) ret;
        } else if (ret instanceof Integer) {
            return ((Integer) ret).doubleValue();  // ✅ 安全转换
        }
        return null;
    }

    @Override
    public void rcSetByArticleId(CacheKey articleKey, Double articleHeat) {
        redisTemplate.opsForValue().set(articleKey.redisKey, articleHeat.doubleValue());
    }

    @Override
    public boolean rcDelByArticleId(CacheKey articleKey) {
        return redisTemplate.delete(articleKey.redisKey);
    }

    @Override
    public Double rcIncrByArticleId(CacheKey articleKey, Double heat) {
        return redisTemplate.opsForValue().increment(articleKey.redisKey, heat);
    }

    @Override
    public Long rcGetRankByArticleId(CacheKey heatKey, String subKey) {
        return redisTemplate.opsForZSet().reverseRank(heatKey.redisKey, subKey);
    }

    @Override
    public Double rcGetScoreByRank(CacheKey heatKey, Long rank) {
        Set<Object> result = redisTemplate.opsForZSet().reverseRange(heatKey.redisKey, rank, rank);
        if (result != null && !result.isEmpty()) {
            String subKey = result.iterator().next().toString();
            return redisTemplate.opsForZSet().score(heatKey.redisKey, subKey);
        }
        return 0.0;
    }

    @Override
    public void rcZAddSortedSetMulti(CacheKey heatKey, List<HeatResponse> articleHeatList) {
        Set<ZSetOperations.TypedTuple<Object>> tuples = new HashSet<>();
        for (HeatResponse articleHeat : articleHeatList) {
            String subKey = articleHeat.getArticleId().toString();
            Double score = articleHeat.getHeat();
            tuples.add(new DefaultTypedTuple<>(subKey, score));
        }
        redisTemplate.opsForZSet().add(heatKey.redisKey, tuples);
    }

    @Override
    public List<HeatResponse> rcZGetSortedSet(CacheKey heatKey, Long start, Long end) {
        List<HeatResponse> heatResponseList = new ArrayList<>();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeWithScores(heatKey.redisKey, start, end);
        if (typedTuples != null) {
            for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
                HeatResponse heatResponse = new HeatResponse();
                heatResponse.setArticleId(Long.parseLong(tuple.getValue().toString()));
                heatResponse.setHeat(tuple.getScore());
                heatResponseList.add(heatResponse);
            }
        }
        return heatResponseList;
    }

    @Override
    public Long rcZRemoveSortedSet(CacheKey heatKey, String subKey) {
        return redisTemplate.opsForZSet().remove(heatKey.redisKey, subKey);
    }

    /*
    解决问题
        1.删除榜单上多余人数时使用分数 OR 排名不能保证绝对原子
        2.并发在 ZADD 无法保证顺序性
    */
    @Override
    public void rcRankPushInSortedSetWithIncr(CacheKey heatKey, String subKey, Double score, Long limit) {
        // Lua 脚本
        String luaScript =
                """
                        local item = ARGV[1]    -- 加分文章
                        local incr_score = tonumber(ARGV[2]) -- 分数
                        local num_limit = tonumber(ARGV[3])  -- ZSET总数限制
                        -- 写ZSET
                        local incr_resp = redis.call("ZADD", KEYS[1], incr_score, item)
                        -- 检查Zset item数量
                        if num_limit > 1 then
                          local elem_num = tonumber(redis.call("ZCARD", KEYS[1]))
                          if elem_num > num_limit then
                            redis.call("ZREMRANGEBYRANK", KEYS[1], 0, elem_num - num_limit - 1)
                          end
                        end
                        return incr_score
                """;
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);
        redisTemplate.execute(redisScript, Collections.singletonList(heatKey.redisKey), subKey, score, limit);
    }

    @Override
    public List<ArticleHeat> getAllByScoreDesc(Long limit) {
        return null;
    }
}