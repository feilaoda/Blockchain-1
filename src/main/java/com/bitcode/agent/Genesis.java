package com.bitcode.agent;

import com.bitcode.agent.config.PremineConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class Genesis extends Block {

    private Map<ByteArray, PremineAccount> premines;

    @Autowired
    private PremineConfig premineConfig;

    @PostConstruct
    public void load() {
        this.index = 0;
        this.previousHash = "0000000000000000000000000000000000000000000000000000000000000000";
        this.creator = "ROOT";
        this.timestamp = 0L;
        this.nonce = 0;
        this.hash = Utils.hash256Str("Genesis");

        premines = new HashMap<>();

        PremineAccount premineAccount = new PremineAccount(premineConfig.getAddress(), premineConfig.getAmount());
        premines.put(ByteArray.of(premineAccount.getAddress()), premineAccount);

    }

    public Map<ByteArray, PremineAccount> getPremines() {
        return premines;
    }

    public void setPremines(Map<ByteArray, PremineAccount> premines) {
        this.premines = premines;
    }
}
