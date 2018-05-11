package com.bitcode.agent.wallet;

import com.bitcode.agent.account.Account;
import com.bitcode.agent.Key;
import com.bitcode.agent.Utils;
import com.bitcode.agent.config.PremineConfig;
import com.bitcode.agent.crypto.AES;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class WalletManager {

    @Autowired
    private PremineConfig premineConfig;

    private Wallet wallet;

    public Wallet load(String file, String password) throws Exception{
        wallet = new Wallet(password);

        String cryptedJson = Files.toString(new File(file), UTF_8);

        String json = new String(AES.decrypt(Utils.hexDecode(cryptedJson), Utils.hash256(password), Utils.hash128(password.getBytes(UTF_8))));


        JSONArray jsonArray = JSONArray.parseArray(json);

        for(Object obj: jsonArray) {
            if(obj instanceof JSONObject) {
                JSONObject jsonObject =  (JSONObject) obj;
                String jsonPrivateKey = jsonObject.getString("privateKey");
                String jsonPublicKey = jsonObject.getString("publicKey");
                String jsonIvKey = jsonObject.getString("ivKey");

                System.out.println("privateKey decrypted hash:"+Utils.hash256Str(jsonPrivateKey));
                System.out.println("decrypted hash:"+Utils.hash256Str(Utils.hexDecode(jsonPrivateKey)));

                byte[] privateKey = AES.decrypt(Utils.hexDecode(jsonPrivateKey), Utils.hash256(password), Utils.hexDecode(jsonIvKey));

                byte[] publicKey = Utils.hexDecode(jsonPublicKey);

                Key key = new Key(privateKey, publicKey);

                Account newAccount = new Account(key);

                System.out.println(Utils.hexEncode(newAccount.getKey().getPublicKey()));
                System.out.println(Utils.hexEncode(newAccount.getKey().getPrivateKey()));

                wallet.addAccount(newAccount);
            }
        }

        return wallet;
    }

    public String write(Wallet wallet) throws IOException {

        JSONArray array = new JSONArray();

        String password = wallet.getPassword();

        for(Account account: wallet.getAccounts().values()) {
            JSONObject jsonObject = new JSONObject();

            Key key = account.getKey();
            byte[] iv = Utils.random(16);

            String publicKey = Utils.hexEncode(key.getPublicKey());
            byte[] cryptedKey = AES.encrypt(key.getPrivateKey(), Utils.hash256(password), iv);

            System.out.println("crypted hash:"+Utils.hash256Str(cryptedKey));
            String privateKey = Utils.hexEncode(cryptedKey);
            System.out.println("privateKey hex :"+privateKey);

            System.out.println("privateKey crypted hash:"+Utils.hash256Str(privateKey));

            byte[] hexDecode = Utils.hexDecode(privateKey);

            System.out.println("hex crypted hash:"+Utils.hash256Str(hexDecode));

            String ivKey = Utils.hexEncode(iv);

            jsonObject.put("privateKey", privateKey);
            jsonObject.put("publicKey", publicKey);
            jsonObject.put("ivKey", ivKey);

            array.add(jsonObject);
        }


        String json = array.toJSONString();

        System.out.println(json);


        String hex1 = Utils.hexEncode(json.getBytes(UTF_8));
        byte[] b1 = Utils.hexEncode(json.getBytes(UTF_8)).getBytes(UTF_8);
        String s2 = new String(b1);

        if(!s2.equals(hex1)) {
            System.out.println("ERRRORR: " + hex1 + " --- " + s2);
        }

        String cryptedJson =  Utils.hexEncode(AES.encrypt(json.getBytes(UTF_8), Utils.hash256(password), Utils.hash128(password.getBytes(UTF_8))));

        Files.write(cryptedJson, new File("wallet.data"), UTF_8);

        return json;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public static void main(String[] args) throws Exception{

        String password = "";
        Wallet wallet = new Wallet(password);

        Account account = new Account(new Key());
        System.out.println(Utils.hexEncode(account.getKey().getPublicKey()));
        System.out.println(Utils.hexEncode(account.getKey().getPrivateKey()));

        wallet.addAccount(account);

        WalletManager manager = new WalletManager();

        String json = manager.write(wallet);

        manager.load("wallet.data", password);
    }

}
