package com.rookiefly.ratelimiter.server.controller;

import com.rookiefly.ratelimiter.server.common.Result;
import com.rookiefly.ratelimiter.server.form.RateLimiterForm;
import com.rookiefly.ratelimiter.server.service.RateLimiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RateLimiterController {

    @Autowired
    private RateLimiterService rateLimiterService;

    @GetMapping(value = "/ratelimiter/{appName}")
    public Result getRateLimiters(@PathVariable String appName) {
        return Result.success(rateLimiterService.getRateLimiters(appName));
    }

    @PostMapping(value = "/ratelimiter")
    public Result saveOrUpdateRateLimiter(@RequestBody RateLimiterForm form) {
        boolean result = rateLimiterService.saveOrUpdateRateLimiter(form);
        return result ? Result.success(result) : Result.fail();
    }

    @DeleteMapping(value = "/ratelimiter/{appName}/{resourceName}")
    public Result deleteRateLimiter(@PathVariable String appName, @PathVariable String resourceName) {
        boolean result = rateLimiterService.deleteRateLimiter(appName, resourceName);
        return result ? Result.success(result) : Result.fail();
    }

    @GetMapping(value = "/ratelimiter/acquire/{resourceName}")
    public Result acquire(@PathVariable String resourceName) {
        boolean acquire = rateLimiterService.acquire(resourceName);
        return acquire ? Result.success(acquire) : Result.fail();
    }
}