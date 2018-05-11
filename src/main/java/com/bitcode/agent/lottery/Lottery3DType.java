package com.bitcode.agent.lottery;

public enum  Lottery3DType {

    LOTTERY_3D_SINGLE(100000100001L, "3D普通注"),
    LOTTERY_3D_BIG_SMALL(100000100002L, "3D大小注");


    long type;
    String name;
    Lottery3DType(long type, String name) {
        this.type = type;
        this.name = name;
    }

    public long getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
