package com.bitcode.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

//@ConfigurationProperties(prefix = "account.premine")
@Component
public class PremineConfig implements Serializable{

    @Value("${account.premine.address}")
    private String address;

    @Value("${account.premine.amount}")
    private long amount;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
