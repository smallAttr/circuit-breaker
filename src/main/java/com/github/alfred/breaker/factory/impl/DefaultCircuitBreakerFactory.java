package com.github.alfred.breaker.factory.impl;

import com.github.alfred.breaker.CircuitBreaker;
import com.github.alfred.config.BreakerConfig;
import com.github.alfred.task.DefaultTask;

import java.util.concurrent.*;

/**
 * @author smallAttr
 * @since 2020-12-08 16:05
 * description: 创建默认断线器对象
 */
public class DefaultCircuitBreakerFactory extends AbstractCircuitBreakerFactory {

    @Override
    protected BreakerConfig getConfig() {
        return new BreakerConfig(10, 50, 10, TimeUnit.SECONDS);
    }

    @Override
    protected Runnable getTask(CircuitBreaker circuitBreaker) {
        return new DefaultTask(circuitBreaker);
    }
}
