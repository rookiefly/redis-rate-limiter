package com.rookiefly.open.ratelimiter.client.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.rate-limiter")
@Data
public class RedisLimiterProperties {

    private String appName;
}