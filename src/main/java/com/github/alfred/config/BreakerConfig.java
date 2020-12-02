package com.github.alfred.config;

import lombok.Data;

import java.util.concurrent.*;

/**
 * @author smallAttr
 * @since 2020-12-02 10:04
 */
@Data
public class BreakerConfig {

    /**
     * 失败阀值
     */
    private int failureThreshold;

    /**
     * 请求总数
     */
    private int totalCount;

    /**
     * 熔断时间
     */
    private int timeout;

    /**
     * 熔断时间单位
     */
    private TimeUnit timeUnit;

    public BreakerConfig(int failureThreshold, int totalCount, int timeout, TimeUnit timeUnit) {
        if (failureThreshold < 1 || totalCount < 1) {
            throw new RuntimeException("失败阀值和请求总数必须大于0");
        }
        if (timeout < 1) {
            throw new RuntimeException("熔断时间必须大于0");
        }
        this.failureThreshold = failureThreshold;
        this.totalCount = totalCount;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }
}
