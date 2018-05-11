package com.bitcode.agent.message;

import com.bitcode.agent.Block;
import com.keeptoken.p2p.peer.Peer;
import com.keeptoken.p2p.peer.network.Connection;
import com.keeptoken.p2p.peer.network.message.Message;

import java.util.List;

public class SyncBlocksAck implements Message{


    private int fromHeight;
    private int lastHeight;

    private List<Block> blocks;

    public SyncBlocksAck(int fromHeight, int latestHeight, List<Block> blocks) {
        this.fromHeight = fromHeight;
        this.blocks = blocks;
    }

    @Override
    public void handle(Peer peer, Connection connection) {

        if(fromHeight < 0) {
            return;
        }

        peer.getAgent().syncBlocks(blocks);
    }

    public int getFromHeight() {
        return fromHeight;
    }

    public void setFromHeight(int fromHeight) {
        this.fromHeight = fromHeight;
    }

    public int getLastHeight() {
        return lastHeight;
    }

    public void setLastHeight(int lastHeight) {
        this.lastHeight = lastHeight;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }


}
