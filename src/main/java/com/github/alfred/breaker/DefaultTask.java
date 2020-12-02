package com.github.alfred.breaker;

import lombok.extern.slf4j.Slf4j;


/**
 * @author smallAttr
 * @since 2020-12-02 11:05
 * description: 断线器默认延迟任务
 */
@Slf4j
public class DefaultTask implements Runnable {

    private CircuitBreaker breaker;

    public DefaultTask(CircuitBreaker breaker) {
        this.breaker = breaker;
    }

    @Override
    public void run() {
        // 重置断线器
        breaker.resetCircuitBreaker();
        log.info("断线器保护期结束，状态切换为关闭状态 breaker [{}]", breaker);
    }
}
