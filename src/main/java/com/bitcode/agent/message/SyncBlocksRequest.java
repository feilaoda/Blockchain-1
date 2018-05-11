package com.bitcode.agent.message;

import com.bitcode.agent.Block;
import com.keeptoken.p2p.peer.Peer;
import com.keeptoken.p2p.peer.network.Connection;
import com.keeptoken.p2p.peer.network.message.Message;

import java.util.List;

public class SyncBlocksRequest implements Message {

    private int fromHeight;

    public SyncBlocksRequest(int fromHeight) {
        this.fromHeight = fromHeight;
    }

    @Override
    public void handle(Peer peer, Connection connection) {

        try {

            Block latestBlock = peer.getAgent().getLatestBlock();
            if (fromHeight > latestBlock.getIndex()) {
                connection.send(new SyncBlocksAck(-1, -1, null));
            }

            List<Block> blockList = peer.getAgent().splitBlocks(fromHeight);

            if (blockList.size() > 0) {
                int latest = blockList.get(blockList.size() - 1).getIndex();
                connection.send(new SyncBlocksAck(fromHeight, latest, blockList));
            }else {
                connection.send(new SyncBlocksAck(-1, -1, null));
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getFromHeight() {
        return fromHeight;
    }

    public void setFromHeight(int fromHeight) {
        this.fromHeight = fromHeight;
    }
}
