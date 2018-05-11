package com.bitcode.agent.account;

import com.bitcode.agent.ByteArray;
import com.bitcode.agent.Key;
import com.bitcode.agent.Utils;
import com.bitcode.agent.crypto.AES;

import java.io.Serializable;

public class Account implements Serializable {

    private ByteArray address;

    private String alias;

    private Key key;

    public Account(ByteArray address) {
        this.address = address;
    }

    public Account(Key key) {
        this.key = key;
        address = ByteArray.of(key.toAddress());
    }


    public ByteArray getAddress() {
        return address;
    }

    public void setAddress(ByteArray address) {
        this.address = address;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Key getKey() {
        return key;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Account) {
            return address.equals(((Account) obj).address);
        }else {
            return super.equals(obj);
        }
    }


    public static void main(String[] args) throws Exception{
        Account account = new Account(new Key());

        System.out.println(account.getAddress().toString());
        byte[] privateKeys = account.getKey().getPrivateKey();

        Key key1 = new Key(account.getKey().getPrivateKey(), account.getKey().getPublicKey());

        System.out.println(Utils.hexEncode(account.getKey().getPrivateKey()));

        String privateKey = "302e020100300506032b6570042204201afb4c5b4015b3222ca8f76e4f1d19a32dedaf4b934b4288cb9ad3def271bb5b";

        String hexKey = Utils.hexEncode(privateKeys);

        byte[] hexDecodeKey = Utils.hexDecode(hexKey);


        String password = "test";

        byte[] iv = Utils.random(16);

        byte[] decKey = AES.encrypt(account.getKey().getPrivateKey(), Utils.hash256(password), iv);


        byte[] key = AES.decrypt(decKey, Utils.hash256(password), iv);


        Account newAccount = new Account(key1);

        System.out.println(account.getAddress());

        System.out.println(newAccount.getAddress());

        System.out.println(Utils.hexEncode(iv));


        System.out.println(Utils.hexEncode(decKey));


        System.out.println(Utils.hexEncode(key));

        System.out.println(Utils.hexEncode(account.getKey().getPrivateKey()));

    }

}
