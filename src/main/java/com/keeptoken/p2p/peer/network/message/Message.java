package com.keeptoken.p2p.peer.network.message;

import com.keeptoken.p2p.peer.Peer;
import com.keeptoken.p2p.peer.network.Connection;

import java.io.Serializable;

/**
 * Interfaces of the messages dispatched between peers in the network
 */
public interface Message extends Serializable {

    void handle(Peer peer, Connection connection);

}
