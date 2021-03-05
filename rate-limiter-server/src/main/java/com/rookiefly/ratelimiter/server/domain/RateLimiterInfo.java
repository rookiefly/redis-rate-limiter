package com.rookiefly.ratelimiter.server.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RateLimiterInfo {

    private Integer id;

    private String resourceName;

    private String appName;

    private Integer maxPermits;

    private Integer rate;

    private String createdBy;

    private String updatedBy;

    private Date createdAt;

    private Date updatedAt;
}