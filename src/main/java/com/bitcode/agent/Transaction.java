package com.bitcode.agent;

import java.io.Serializable;

public class Transaction implements Serializable {

    private long timestamp;

    private ByteArray from;

    private ByteArray to;

    private long nonce;

    private byte[] data;

    public Transaction(long timestamp, ByteArray from, ByteArray to, long nonce, byte[] data) {
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.nonce = nonce;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ByteArray getFrom() {
        return from;
    }

    public void setFrom(ByteArray from) {
        this.from = from;
    }

    public ByteArray getTo() {
        return to;
    }

    public void setTo(ByteArray to) {
        this.to = to;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
