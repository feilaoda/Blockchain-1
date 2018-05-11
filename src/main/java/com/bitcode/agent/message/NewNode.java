package com.bitcode.agent.message;

import com.keeptoken.p2p.peer.Peer;
import com.keeptoken.p2p.peer.network.Connection;
import com.keeptoken.p2p.peer.network.message.Message;

public class NewNode implements Message{

    private String host;

    private int port;

    public NewNode(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void handle(Peer peer, Connection connection) {

        peer.getAgent().addPeer(host, port);

    }
}
