package com.bitcode.agent.account;

import com.bitcode.agent.ByteArray;
import com.bitcode.agent.Genesis;
import com.bitcode.agent.PremineAccount;
import com.bitcode.agent.wallet.WalletManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigInteger;

@Component
public class AccountManager {

    @Autowired
    private Genesis genesis;

    @Autowired
    private WalletManager walletManager;

    @Autowired
    AccountDB accountDB;

    @PostConstruct
    public void load() {

        for(PremineAccount premineAccount: genesis.getPremines().values()) {
            accountDB.add(ByteArray.of(premineAccount.getAddress()), BigInteger.valueOf(premineAccount.getAmount()));
        }


    }

}
