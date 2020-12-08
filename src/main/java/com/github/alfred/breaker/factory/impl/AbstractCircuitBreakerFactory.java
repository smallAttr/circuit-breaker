package com.github.alfred.breaker.factory.impl;

import com.github.alfred.breaker.CircuitBreaker;
import com.github.alfred.breaker.factory.CircuitBreakerFactory;
import com.github.alfred.config.BreakerConfig;
import com.github.alfred.fallback.FallbackFactory;
import com.github.alfred.fallback.impl.DefaultFallbackFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author smallAttr
 * @since 2020-12-08 15:48
 */
public abstract class AbstractCircuitBreakerFactory implements CircuitBreakerFactory {

    private static final Map<String, Object> breakerCache = new ConcurrentHashMap<>(16);

    private static final String DEFAULT_MODULE = "default&";

    @Override
    public CircuitBreaker getObject() {
        String simpleName = CircuitBreaker.class.getSimpleName();
        String name = this.getModuleName() + transformedName(simpleName);
        // 从缓存中获取断线器
        CircuitBreaker circuitBreaker = (CircuitBreaker) breakerCache.get(name);
        // 获取不到就创建一个断线器
        if (circuitBreaker == null) {
            // 获取对象
            circuitBreaker = new CircuitBreaker();
            // 设置断线器规则信息
            circuitBreaker.setBreakerConfig(getConfig());
            // 设置延迟任务
            circuitBreaker.setTask(getTask(circuitBreaker));
            // 设置回调处理
            circuitBreaker.setFallbackFactory(getFallbackFactory());
            // 个性化定制断线器
            circuitBreakerHook(circuitBreaker);
            breakerCache.put(name, circuitBreaker);
        }
        return circuitBreaker;
    }

    /**
     * 定义断线器配置
     *
     * @return
     */
    protected abstract BreakerConfig getConfig();

    /**
     * 定义断线器任务
     *
     * @param circuitBreaker
     * @return
     */
    protected abstract Runnable getTask(CircuitBreaker circuitBreaker);

    /**
     * 个性化定制断线器
     *
     * @param circuitBreaker
     */
    protected void circuitBreakerHook(CircuitBreaker circuitBreaker) {
        // for subclass
    }

    /**
     * 定义业务名（按着实际场景区分，不同场景对应不同的断线器实例）
     *
     * @return
     */
    protected String getModuleName() {
        return DEFAULT_MODULE;
    }

    /**
     * 回调工厂
     *
     * @return
     */
    protected FallbackFactory getFallbackFactory() {
        return new DefaultFallbackFactory();
    }

    /**
     * 获取首字母小写的类名
     *
     * @param simpleName
     * @return
     */
    private String transformedName(String simpleName) {
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }
}
