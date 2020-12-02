package com.github.alfred.enums;

/**
 * @author smallAttr
 * @since 2020-12-01 22:40
 * description: 熔断器状态
 */
public enum BreakerStatus {

    CLOSE(0, "关闭"),
    OPEN(1, "开启"),
    ;

    private int value;

    private String description;

    BreakerStatus(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
