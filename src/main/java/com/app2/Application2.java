package com.app2;

import com.bitcode.agent.ProofOfWork;
import com.bitcode.agent.wallet.Wallet;
import com.bitcode.agent.wallet.WalletManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.bitcode")
public class Application2 {

    public static void main(String[] args) {
        ProofOfWork.mDiffcult = 21;

        String walletName = "wallet.data";
        if(args.length>1) {
            walletName = args[1];
        };
        String password = "";
        if(args.length>2) {
            password = args[2];
        }



        ApplicationContext ctx = SpringApplication.run(Application2.class, args);

        WalletManager walletManager = ctx.getBean("walletManager", WalletManager.class);

        try {
            Wallet wallet = walletManager.load(walletName, password);



        }catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }
}