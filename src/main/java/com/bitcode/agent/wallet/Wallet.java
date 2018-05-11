package com.bitcode.agent.wallet;

import com.bitcode.agent.account.Account;
import com.bitcode.agent.ByteArray;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Wallet {

    private String password;

    private final Map<ByteArray, Account> accounts = Collections.synchronizedMap(new LinkedHashMap<>());


    public Wallet(String password) {
        this.password = password;
    }



    public void addAccount(Account account) {
        if(accounts.containsKey(account.getAddress())) {

        }else {
            accounts.put(account.getAddress(), account);
        }
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Map<ByteArray, Account> getAccounts() {
        return accounts;
    }
}
