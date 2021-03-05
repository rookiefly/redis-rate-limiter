package com.rookiefly.open.ratelimiter.client.autoconfigure;

import com.rookiefly.open.ratelimiter.client.RateLimiterClient;
import com.rookiefly.open.ratelimiter.client.RateLimiterFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean(StringRedisTemplate.class)
@EnableConfigurationProperties(RedisLimiterProperties.class)
public class RateLimiterAutoConfiguration {

    @Resource
    private RedisLimiterProperties redisLimiterProperties;

    private StringRedisTemplate stringRedisTemplate;

    public RateLimiterAutoConfiguration(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "rateLimiterClient")
    public RateLimiterClient rateLimiterClient() {
        return new RateLimiterClient(stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(name = "rateLimiterFactory")
    public RateLimiterFactory rateLimiterFactory() {
        return new RateLimiterFactory(stringRedisTemplate, redisLimiterProperties.getAppName());
    }
}
