package com.bitcode.agent;

import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Utils {

    private static final char[] intToHex = "0123456789abcdef".toCharArray();
    private static final int[] hexToInt = new int[128];

    static {
        for (byte i = 0; i < 16; i++) {
            if (i < 10) {
                hexToInt['0' + i] = i;
            } else {
                hexToInt['a' + i - 10] = i;
                hexToInt['A' + i - 10] = i;
            }
        }
    }

    private static SecureRandom secureRandom = new SecureRandom();


    public static String hash256Str(String text) {
        byte[] bytes = hash256(text);
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();

    }

    public static String hash256Str(byte[] data) {
        byte[] bytes = hash256(data);
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();

    }

    public static byte[] hash256(String text) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(text.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

//        final byte bytes[] = digest.digest(text.getBytes());
//        final StringBuilder hexString = new StringBuilder();
//        for (final byte b : bytes) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
    }

    public static byte[] hash256(byte[] one, byte[] two) {
        byte[] all = new byte[one.length + two.length];
        System.arraycopy(one, 0, all, 0, one.length);
        System.arraycopy(two, 0, all, one.length, two.length);

        return hash256(all);
    }

    public static byte[] hash256(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] hash160(byte[] input) {
        byte[] h256 = hash256(input);

        RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(h256, 0, h256.length);
        byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return out;
    }

    public static byte[] hash128(byte[] input) {
        byte[] h256 = hash256(input);

        RIPEMD128Digest digest = new RIPEMD128Digest();
        digest.update(h256, 0, h256.length);
        byte[] out = new byte[16];
        digest.doFinal(out, 0);
        return out;
    }

    public static String hexEncode(byte[] raw) {
        char[] hex = new char[raw.length * 2];

        for (int i = 0; i < raw.length; i++) {
            hex[i * 2] = intToHex[(raw[i] & 0xF0) >> 4];
            hex[i * 2 + 1] = intToHex[raw[i] & 0x0F];
        }

        return new String(hex);
    }

    public static byte[] hexDecode(String hex) throws RuntimeException {
        if (hex == null || !hex.matches("([0-9a-fA-F]{2})*")) {
            throw new RuntimeException("Invalid hex string");
        }

        byte[] raw = new byte[hex.length() / 2];

        char[] chars = hex.toCharArray();
        for (int i = 0; i < chars.length; i += 2) {
            raw[i / 2] = (byte) ((hexToInt[chars[i]] << 4) + hexToInt[chars[i + 1]]);
        }

        return raw;
    }

    public static byte[] hexDecode0x(String hex) throws RuntimeException {
        if (hex != null && hex.startsWith("0x")) {
            return hexDecode(hex.substring(2));
        } else {
            return hexDecode(hex);
        }
    }


    public static byte[] random(int n) {
        byte[] bytes = new byte[n];
        secureRandom.nextBytes(bytes);

        return bytes;
    }


}
