package com.rookiefly.ratelimiter.server.mapper;


import com.rookiefly.ratelimiter.server.domain.RateLimiterInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RateLimiterInfoMapper {

    List<RateLimiterInfo> selectAll();

    List<RateLimiterInfo> selectByAppName(@Param("appName") String appName);

    void saveOrUpdate(@Param("appName") String appName, @Param("resourceName") String resourceName, @Param("maxPermits") Integer maxPermits, @Param("rate") Integer rate);

    RateLimiterInfo selectByName(@Param("appName") String appName, @Param("resourceName") String resourceName);

    void deleteByName(@Param("appName") String appName, @Param("resourceName") String resourceName);
}