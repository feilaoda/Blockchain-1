package com.bitcode.agent.lottery;

public enum LotteryType {

    LOTTERY_3D(1000001);


    int type;

    LotteryType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
