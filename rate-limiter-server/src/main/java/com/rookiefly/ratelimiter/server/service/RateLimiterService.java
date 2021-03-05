package com.rookiefly.ratelimiter.server.service;

import com.google.common.collect.Lists;
import com.rookiefly.open.ratelimiter.client.RateLimiterClient;
import com.rookiefly.open.ratelimiter.client.RateLimiterFactory;
import com.rookiefly.ratelimiter.server.domain.RateLimiterInfo;
import com.rookiefly.ratelimiter.server.form.RateLimiterForm;
import com.rookiefly.ratelimiter.server.mapper.RateLimiterInfoMapper;
import com.rookiefly.ratelimiter.server.vo.RateLimiterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RateLimiterService implements InitializingBean {

    @Resource
    private RateLimiterClient rateLimiterClient;

    @Resource
    private RateLimiterFactory rateLimiterFactory;

    @Resource
    private RateLimiterInfoMapper rateLimiterInfoMapper;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public List<RateLimiterVO> getRateLimiters(String appName) {
        List<RateLimiterInfo> rateLimiterInfoList = rateLimiterInfoMapper.selectByAppName(appName);
        if (CollectionUtils.isEmpty(rateLimiterInfoList)) {
            return null;
        }

        List<String> resourceNameList = rateLimiterInfoList.stream().map(RateLimiterInfo::getResourceName).collect(Collectors.toList());
        List<Map<String, String>> rateLimiterListFromRedis = rateLimiterClient.getRateLimiters(appName, resourceNameList);

        List<RateLimiterVO> rateLimiterRespList = Lists.newArrayListWithCapacity(rateLimiterInfoList.size());

        for (int i = 0; i < rateLimiterListFromRedis.size(); i++) {
            RateLimiterInfo rateLimiterInfo = rateLimiterInfoList.get(i);
            Map<String, String> rateLimiterMap = rateLimiterListFromRedis.get(i);
            rateLimiterRespList.add(RateLimiterVO.builder()
                    .resourceName(rateLimiterInfo.getResourceName())
                    .appName(rateLimiterMap.get("app_name"))
                    .maxPermits(Integer.parseInt(rateLimiterMap.get("max_permits")))
                    .currPermits(Integer.parseInt(rateLimiterMap.get("curr_permits")))
                    .rate(Integer.parseInt(rateLimiterMap.get("rate")))
                    .lastPermitTimestamp(rateLimiterMap.get("last_mill_second"))
                    .build());
        }
        return rateLimiterRespList;
    }

    public boolean saveOrUpdateRateLimiter(RateLimiterForm form) {
        String resourceName = form.getResourceName();
        String appName = form.getAppName();

        rateLimiterInfoMapper.saveOrUpdate(appName, resourceName, form.getMaxPermits(), form.getRate());
        rateLimiterClient.reload(appName, resourceName, form.getMaxPermits(), form.getRate());
        return true;
    }

    public boolean deleteRateLimiter(String appName, String resourceName) {
        RateLimiterInfo rateLimiterInfo = rateLimiterInfoMapper.selectByName(appName, resourceName);
        if (rateLimiterInfo != null) {
            rateLimiterInfoMapper.deleteByName(appName, resourceName);
            rateLimiterClient.delete(appName, resourceName);
        }
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        executorService.scheduleAtFixedRate(() -> {
            try {
                log.info("diff db and redis job start.....");
                List<RateLimiterInfo> rateLimiterInfoList = rateLimiterInfoMapper.selectAll();
                for (RateLimiterInfo rateLimiterInfo : rateLimiterInfoList) {
                    rateLimiterClient.reload(rateLimiterInfo.getAppName(), rateLimiterInfo.getResourceName(), rateLimiterInfo.getMaxPermits(), rateLimiterInfo.getRate());
                }
                log.info("diff db and redis job end.....");
            } catch (Exception e) {
                log.error("diff db and redis error.....", e);
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    public boolean acquire(String resourceName) {
        return rateLimiterFactory.getRateLimiter(resourceName).acquire();
    }
}
