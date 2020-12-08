package com.github.alfred.fallback.impl;

import com.github.alfred.fallback.FallbackFactory;

/**
 * @author smallAttr
 * @since 2020-12-02 10:26
 * description: 断线器启用默认处理逻辑
 */
public class DefaultFallbackFactory implements FallbackFactory<Void> {

    @Override
    public Void fallback() {
        System.out.println("熔断已开启，拒绝访问");
        return null;
    }
}
