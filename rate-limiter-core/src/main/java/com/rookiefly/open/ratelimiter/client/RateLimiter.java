package com.rookiefly.open.ratelimiter.client;

import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {

    private Logger logger = LoggerFactory.getLogger(RateLimiter.class);

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisScript<Long> rateLimiterClientLua;

    private final String appName;

    private final String resourceName;

    private static final AtomicLong LOCAL_TOKEN_HOLDER = new AtomicLong(0);

    private static final int DEFAULT_BATCH_SIZE = 50;

    public RateLimiter(StringRedisTemplate stringRedisTemplate,
                       RedisScript<Long> rateLimiterClientLua,
                       String appName,
                       String resourceName) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rateLimiterClientLua = rateLimiterClientLua;
        this.appName = appName;
        this.resourceName = resourceName;
    }

    /**
     * 获取令牌，访问redis异常算做成功
     * 默认的permits为1
     *
     * @return
     */
    public boolean acquire() {
        Token token = acquireToken(appName, resourceName);
        return token.isPass() || token.isAccessRedisFail();
    }

    /**
     * 获取{@link Token}
     * 默认的permits为1
     *
     * @param appName
     * @param resourceName
     * @return
     */
    public Token acquireToken(String appName, String resourceName) {
        return acquireToken(appName, resourceName, 1);
    }

    /**
     * 获取{@link Token}
     *
     * @param appName
     * @param resourceName
     * @param permits
     * @return
     */
    public Token acquireToken(String appName, String resourceName, Integer permits) {
        Token token;
        try {
            Long currMillSecond = stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.time());
            Long acquire = stringRedisTemplate.execute(rateLimiterClientLua, ImmutableList.of(getKey(appName, resourceName)), RateLimiterConstants.RATE_LIMITER_ACQUIRE_METHOD, permits.toString(), currMillSecond.toString(), appName);

            if (acquire == 1) {
                token = Token.PASS;
            } else if (acquire == -1) {
                token = Token.FAIL;
            } else {
                logger.error("no rate limit config for appName={}, resourceName={}", appName, resourceName);
                token = Token.NO_CONFIG;
            }
        } catch (Throwable e) {
            logger.error("get rate limit token from redis error, appName={}, resourceName={}, exception={}", appName, resourceName, e);
            token = Token.ACCESS_REDIS_ERROR;
        }
        return token;
    }

    private String getKey(String appName, String resourceName) {
        return String.format(RateLimiterConstants.RATE_LIMITER_KEY_FORMAT, appName, resourceName);
    }
}
