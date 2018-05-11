package com.bitcode.agent.message;

import com.bitcode.agent.Block;
import com.keeptoken.p2p.peer.Peer;
import com.keeptoken.p2p.peer.network.Connection;
import com.keeptoken.p2p.peer.network.message.Message;

import java.util.Collections;

public class NewBlockMessage implements Message{


    private Block block;

    public NewBlockMessage(Block block) {
        this.block = block;
    }


    @Override
    public void handle(Peer peer, Connection connection) {
        Block latestBlock = peer.getAgent().getLatestBlock();
        if(latestBlock.getIndex()+1<block.getIndex()) {
            connection.send(new SyncBlocksRequest(latestBlock.getIndex()+1));
        }else {
            peer.getAgent().syncBlocks(Collections.singletonList(block));
        }
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }
}
