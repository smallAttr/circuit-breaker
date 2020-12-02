package com.github.alfred.breaker;

import com.github.alfred.config.BreakerConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author smallAttr
 * @since 2020-12-01 22:56
 * description: 断线器执行器
 */
@Slf4j
public class CircuitBreakerRunner {

    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    private static final Map<String, Object> breakerCache = new ConcurrentHashMap<>(16);


    public static void main(String[] args) throws Exception {
        // 获取断线器实例
        CircuitBreaker circuitBreaker = getBreaker();

        // 业务逻辑代码声明
        Runnable runnable = () -> {
            int nextInt = new Random().nextInt(100);
            if (nextInt % 2 != 0) {
                System.out.println("抛出异常");
                throw new RuntimeException("抛出异常");
            } else {
                // todo 正常业务逻辑处理
                System.out.println("执行正常业务逻辑");
            }
        };

        for (int i = 0; i < 100; i++) {
            executor.execute(() -> run(circuitBreaker, runnable));
            System.out.println("执行次数：" + (i+1));
            Thread.sleep(1000);
        }
    }

    public static void run(CircuitBreaker breaker, Runnable runnable) {
        synchronized (CircuitBreakerRunner.class) {
            log.info("开始执行run方法");
            // 判断断线器状态是否已开启
            if (breaker.isOpen()) {
                // 调用fallback
                breaker.getFallbackHandler().fallback();
                log.warn("failureThreshold: [{}] -----> totalCount: [{}]", breaker.getFailureThreshold(), breaker.getTotalCount());
                return;
            }
            try {
                runnable.run();
            } catch (Exception e) {
                breaker.failureThresholdIncrement();
                System.out.println("failureThreshold====" + breaker.getFailureThreshold());
            } finally {
                breaker.totalCountIncrement();
                System.out.println("totalCount====" + breaker.getTotalCount());
            }
            log.info("结束执行run方法\n===================");
        }
    }

    /**
     * 从缓存中获取断线器，如果获取不到就创建断线器实例
     * @return
     */
    private static CircuitBreaker getBreaker() {
        String simpleName = CircuitBreaker.class.getSimpleName();
        String name = transformedName(simpleName);
        // 从缓存中获取断线器
        CircuitBreaker circuitBreaker = (CircuitBreaker) breakerCache.get(name);
        if (circuitBreaker == null) {
            // 获取单例对象
            circuitBreaker = CircuitBreaker.getInstance();
            // 设置断线器规则信息
            BreakerConfig config = new BreakerConfig(10, 50, 10, TimeUnit.SECONDS);
            circuitBreaker.setBreakerConfig(config);
            // 设置延迟任务
            DefaultTask defaultTask = new DefaultTask(circuitBreaker);
            circuitBreaker.setTask(defaultTask);
            breakerCache.put(name, circuitBreaker);
        }

        return circuitBreaker;
    }

    private static String transformedName(String simpleName) {
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }
}
