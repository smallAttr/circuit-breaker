package com.github.alfred.breaker;

import com.github.alfred.breaker.factory.impl.DefaultCircuitBreakerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * @author smallAttr
 * @since 2020-12-08 10:26
 */
@Slf4j
public class CircuitBreakerRunnerTests {

    private CircuitBreaker circuitBreaker;

    private static final ExecutorService executor = Executors.newFixedThreadPool(5);

    @Before
    public void initCircuitBreaker() {
        circuitBreaker = new DefaultCircuitBreakerFactory().getObject();
    }

    @Test
    public void circuitBreakerRunner() throws Exception {
        // 业务逻辑代码声明
        Runnable runnable = () -> {
            int nextInt = new Random().nextInt(100);
            if (nextInt % 2 != 0) {
                throw new RuntimeException("抛出异常");
            } else {
                // todo 正常业务逻辑处理
                System.out.println("执行正常业务逻辑");
            }
        };

        final int count = 100;
        for (int i = 0; i < count; i++) {
            executor.execute(() -> CircuitBreakerRunner.run(circuitBreaker, runnable));
            Thread.sleep(1000);
        }
    }
}
