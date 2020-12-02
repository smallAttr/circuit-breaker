package com.github.alfred.breaker;

import com.github.alfred.config.BreakerConfig;
import com.github.alfred.enums.BreakerStatus;
import com.github.alfred.fallback.FallbackFactory;
import com.github.alfred.fallback.impl.DefaultFallback;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * @author smallAttr
 * @since 2020-12-01 22:22
 * description: 断线器
 */
@Getter
@Setter
@Slf4j
public class CircuitBreaker {

    private static volatile CircuitBreaker INSTANCE;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    /**
     * 失败阀值
     */
    private AtomicInteger failureThreshold = new AtomicInteger(0);

    /**
     * 请求总数
     */
    private AtomicInteger totalCount = new AtomicInteger(0);

    /**
     * 断线器状态
     */
    private BreakerStatus status = BreakerStatus.CLOSE;

    /**
     * 断线器配置信息
     */
    private BreakerConfig breakerConfig;

    /**
     * 断线器启用处理类
     */
    private FallbackFactory fallbackHandler = new DefaultFallback();

    /**
     * 定时任务
     */
    private Runnable task;


    private CircuitBreaker() {
    }

    /**
     * 获取CircuitBreaker对象
     * @return
     */
    public static CircuitBreaker getInstance() {
        if (INSTANCE == null) {
            synchronized (CircuitBreaker.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CircuitBreaker();
                }
            }
        }
        return INSTANCE;
    }

    public boolean isOpen() {
        return this.status == BreakerStatus.OPEN;
    }

    public void failureThresholdIncrement() {
        this.failureThreshold.incrementAndGet();
    }

    public void totalCountIncrement() {
        int currentFailureThreshold = this.failureThreshold.get();
        int currentTotalCount = this.totalCount.incrementAndGet();
        // 熔断开启条件
        if (currentFailureThreshold >= breakerConfig.getFailureThreshold() && currentTotalCount <= breakerConfig.getTotalCount()) {
            this.status = BreakerStatus.OPEN;
            // 进入保护期（创建延迟任务，达到指定时间状态变更为关闭状态）
            executorService.schedule(task, breakerConfig.getTimeout(), breakerConfig.getTimeUnit());
        } else if (currentFailureThreshold < breakerConfig.getFailureThreshold() && currentTotalCount == breakerConfig.getTotalCount()) {
            // 请求总数达到指定值 而失败阀值未达到指定值时，重置下断线器
            resetCircuitBreaker();
        } else {
            // do nothing
        }
    }

    public void resetCircuitBreaker() {
        log.info("<-------------重置断线器---------------->");
        this.failureThreshold = new AtomicInteger(0);
        this.totalCount = new AtomicInteger(0);
        this.status = BreakerStatus.CLOSE;
    }

}
