/**
 * Copyright (c) 2017-2018 The Semux Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package com.bitcode.agent;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.io.Serializable;

public class ByteArray implements Comparable<ByteArray>, Serializable {
    private final byte[] data;
    private final int hash;

    public ByteArray(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Input data can not be null");
        }
        this.data = data;
        this.hash = Arrays.hashCode(data);
    }

    public static ByteArray of(byte[] data) {
        return new ByteArray(data);
    }

    public int length() {
        return data.length;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof ByteArray) && Arrays.areEqual(data, ((ByteArray) other).data);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public int compareTo(ByteArray o) {
        return Arrays.compareUnsigned(data, o.data);
    }

    @Override
    public String toString() {
        return Utils.hexEncode(data);
    }

    public static class ByteArrayKeyDeserializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext context) throws IOException {
            return new ByteArray(Utils.hexDecode0x(key));
        }
    }
}