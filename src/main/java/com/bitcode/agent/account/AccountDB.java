package com.bitcode.agent.account;

import com.bitcode.agent.ByteArray;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountDB {

    private static final Object lock = new Object();

    private Map<ByteArray, AccountAmount> accountAmountMap;

    public AccountDB() {
        accountAmountMap = new ConcurrentHashMap<>();
    }

    public void add(ByteArray address, BigInteger amount) {
        synchronized (lock) {
            if(accountAmountMap.containsKey(address)) {
                AccountAmount acc = accountAmountMap.get(address);
                BigInteger v = acc.getAmount().add(amount);
                acc.setAmount(v);
            }else {
                accountAmountMap.put(address, new AccountAmount(address, amount));
            }
        }
    }

    public boolean has(String address) {
        ByteArray byteArray = ByteArray.of(address.getBytes());
        return accountAmountMap.containsKey(byteArray);
    }


    public AccountAmount get(String address) {
        ByteArray byteArray = ByteArray.of(address.getBytes());
        return accountAmountMap.get(byteArray);
    }


}
