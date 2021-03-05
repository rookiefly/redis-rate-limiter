package com.rookiefly.ratelimiter.server.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateLimiterVO {
    
    private String appName;

    private String resourceName;

    private Integer maxPermits;

    private Integer rate;

    private Integer currPermits;

    private String lastPermitTimestamp;
}
