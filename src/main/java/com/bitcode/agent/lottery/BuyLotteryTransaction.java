package com.bitcode.agent.lottery;

import com.bitcode.agent.ByteArray;
import com.bitcode.agent.Transaction;

import java.math.BigInteger;

public class BuyLotteryTransaction extends Transaction {
    private Integer lotteryIndex; //乐透奖期

    private Long lotteryType;

    private String value;

    private BigInteger amount;

    private Integer count;

    public BuyLotteryTransaction(long timestamp, ByteArray from, ByteArray to, long nonce, byte[] data) {
        super(timestamp, from, to, nonce, data);
    }

    public BuyLotteryTransaction(long timestamp, ByteArray from, ByteArray to, long nonce, byte[] data, Integer lotteryIndex, Long lotteryType,  String value, Integer count) {
        super(timestamp, from, to, nonce, data);
        this.lotteryIndex = lotteryIndex;
        this.lotteryType = lotteryType;
        this.value = value;
        this.count = count;
    }

    public Integer getLotteryIndex() {
        return lotteryIndex;
    }

    public void setLotteryIndex(Integer lotteryIndex) {
        this.lotteryIndex = lotteryIndex;
    }

    public Long getLotteryType() {
        return lotteryType;
    }

    public void setLotteryType(Long lotteryType) {
        this.lotteryType = lotteryType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
