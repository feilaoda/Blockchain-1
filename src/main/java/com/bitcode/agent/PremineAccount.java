package com.bitcode.agent;

import org.springframework.stereotype.Component;

import java.io.Serializable;

public class PremineAccount implements Serializable{

    private long amount;

    private byte[] address;

    public PremineAccount(String address, long amount) {
        this.address = address.getBytes();
        this.amount = amount;
    }

    public PremineAccount(byte[] address, long amount) {
        this.address = address;
        this.amount = amount;
    }

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
