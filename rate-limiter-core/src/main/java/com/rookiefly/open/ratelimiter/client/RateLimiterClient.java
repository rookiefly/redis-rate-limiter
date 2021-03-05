package com.rookiefly.open.ratelimiter.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.Map;

public class RateLimiterClient {

    private Logger logger = LoggerFactory.getLogger(RateLimiterClient.class);

    private StringRedisTemplate stringRedisTemplate;

    private RedisScript<Long> rateLimiterClientLua;

    public RateLimiterClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rateLimiterClientLua = rateLimiterLua();
    }

    private DefaultRedisScript<Long> rateLimiterLua() {
        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        defaultRedisScript.setLocation(new ClassPathResource("redis_rate_limiter.lua"));
        defaultRedisScript.setResultType(Long.class);
        return defaultRedisScript;
    }

    /**
     * 初始化RateLimiter配置
     *
     * @param appName
     * @param resourceName
     * @param maxPermits
     * @param rate
     * @return
     */
    public void reload(String appName, String resourceName, Integer maxPermits, Integer rate) {
        stringRedisTemplate.execute(rateLimiterClientLua,
                ImmutableList.of(getKey(appName, resourceName)),
                RateLimiterConstants.RATE_LIMITER_INIT_METHOD, maxPermits + "", rate + "", appName);
    }

    /**
     * 初始化RateLimiter配置
     *
     * @param appName
     * @param resourceName
     * @return
     */
    public void delete(String appName, String resourceName) {
        stringRedisTemplate.execute(rateLimiterClientLua,
                ImmutableList.of(getKey(appName, resourceName)),
                RateLimiterConstants.RATE_LIMITER_DELETE_METHOD);
    }

    public List<Map<String, String>> getRateLimiters(String appName, List<String> resourceNameList) {
        List<Object> rateLimiterList = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            StringRedisConnection stringRedisConn = (StringRedisConnection) connection;
            for (String resourceName : resourceNameList) {
                stringRedisConn.hGetAll(getKey(appName, resourceName));
            }
            return null;
        });

        List<Map<String, String>> rateLimiterMapList = Lists.newLinkedList();
        for (int i = 0; i < rateLimiterList.size(); i++) {
            Object object = rateLimiterList.get(i);
            Map<String, String> rateLimiterMap = (Map<String, String>) object;
            rateLimiterMapList.add(rateLimiterMap);
        }

        return rateLimiterMapList;
    }

    private String getKey(String appName, String resourceName) {
        return String.format(RateLimiterConstants.RATE_LIMITER_KEY_FORMAT, appName, resourceName);
    }
}
