package com.github.alfred.breaker;

import lombok.extern.slf4j.Slf4j;

/**
 * @author smallAttr
 * @since 2020-12-01 22:56
 * description: 断线器执行器
 */
@Slf4j
public class CircuitBreakerRunner {

    /**
     * 断线器执行方法
     *
     * @param breaker
     * @param runnable
     */
    public static void run(CircuitBreaker breaker, Runnable runnable) {
        log.info("<============开始执行run方法============>");
        // 判断断线器状态是否已开启
        if (breaker.isOpen()) {
            // 调用fallback
            breaker.getFallbackFactory().fallback();
            log.warn("failureThreshold: [{}] -----> totalCount: [{}]", breaker.getFailureThreshold(), breaker.getTotalCount());
            return;
        }
        try {
            runnable.run();
        } catch (Exception e) {
            log.error("exception ", e);
            breaker.failureThresholdIncrement();
            System.out.println("failureThreshold====" + breaker.getFailureThreshold());
        } finally {
            breaker.totalCountIncrement();
            System.out.println("totalCount====" + breaker.getTotalCount());
        }
        log.info("结束执行run方法");
    }


}
