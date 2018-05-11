package com.bitcode.agent.account;

import com.bitcode.agent.ByteArray;

import java.math.BigInteger;

public class AccountAmount extends Account{
    private BigInteger amount;

    public AccountAmount(ByteArray address) {
        super(address);
        amount = BigInteger.ZERO;
    }

    public AccountAmount(ByteArray address, BigInteger amount) {
        super(address);
        amount = amount;
    }

    public BigInteger getAmount() {
        return amount;
    }

    public void setAmount(BigInteger amount) {
        this.amount = amount;
    }
}
