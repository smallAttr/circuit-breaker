package com.github.alfred.breaker.factory;

import com.github.alfred.breaker.CircuitBreaker;

/**
 * @author smallAttr
 * @since 2020-12-08 15:46
 */
@FunctionalInterface
public interface CircuitBreakerFactory {

    /**
     * 获取断线器对象
     *
     * @return
     */
    CircuitBreaker getObject();
}
