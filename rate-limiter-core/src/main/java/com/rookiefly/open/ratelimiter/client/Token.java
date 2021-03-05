package com.rookiefly.open.ratelimiter.client;

public enum Token {

    /**
     * 表示获取token成功
     */
    PASS,
    /**
     * 表示没有配置元数据
     */
    NO_CONFIG,
    /**
     * 表示获取token失败
     */
    FAIL,
    /**
     * 表示访问redis异常
     */
    ACCESS_REDIS_ERROR;

    public boolean isPass() {
        return this.equals(PASS);
    }

    public boolean isFusing() {
        return this.equals(FAIL);
    }

    public boolean isAccessRedisFail() {
        return this.equals(ACCESS_REDIS_ERROR);
    }

    public boolean isNoConfig() {
        return this.equals(NO_CONFIG);
    }
}
