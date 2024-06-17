package com.toone.system.generator.storage;

public enum LocalStorageNameStrategyEnum {
    /**
     * 文件名策略
     * 1. seqAndOriginalName: 序列号加原文件名
     * 2. originalNameAndSeq: 原文件名+序列号
     * 3. Seq: 序列号（项目启动时间戳的自增）
     * 4. UUID: 去除短杠'-'的UUID
     */
    UUID("uuid"),
    SEQ("seq"),
    SEQ_AND_ORIGINAL_NAME("seqAndOriginalName"),
    ORIGINAL_NAME_AND_SEQ("originalNameAndSeq");

    private final String strategy;
    LocalStorageNameStrategyEnum(String strategy){
        this.strategy = strategy;
    }

    public String getStrategy() {
        return strategy;
    }
}

