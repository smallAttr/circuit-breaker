package com.github.alfred.fallback;

/**
 * @author smallAttr
 * @since 2020-12-02 10:22
 */
@FunctionalInterface
public interface FallbackFactory<T> {

    /**
     * 断线器启动后处理逻辑
     * @return
     */
    T fallback();

}
