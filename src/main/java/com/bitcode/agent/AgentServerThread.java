package com.bitcode.agent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static com.bitcode.agent.AgentMessage.MESSAGE_TYPE.*;

public class AgentServerThread extends Thread {
    private Socket client;
    private final Agent agent;

    AgentServerThread(final Agent agent, final Socket client) {
        super(agent.getName() + System.currentTimeMillis());
        this.agent = agent;
        this.client = client;
    }

    @Override
    public void run() {
        try (

                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
                    AgentMessage message = new AgentMessage.MessageBuilder().withSenderAgent(agent.getAddress() + ":" + agent.getPort()).withSender(agent.getPort()).withType(READY)
                    .withPeers(agent.getPeers()).build();
                    out.writeObject(message);
            Object fromClient;
            while ((fromClient = in.readObject()) != null) {
                if (fromClient instanceof AgentMessage) {
                    final AgentMessage msg = (AgentMessage) fromClient;
                    System.out.println(String.format("%d received: %s", agent.getPort(), fromClient.toString()));
                    if (INFO_NEW_BLOCK == msg.type) {
                        if (msg.blocks.isEmpty() || msg.blocks.size() > 1) {
                            System.err.println("Invalid block received: " + msg.blocks);
                        }
                        synchronized (agent) {
                            Block block = msg.blocks.get(0);
                            if (block.getIndex() > agent.getLatestBlock().getIndex()) {
                                agent.broadcast(REQ_ALL_BLOCKS, null);
                            } else {
                                agent.addBlock(msg.blocks.get(0));
                            }
                        }
                        break;
                    } else if (REQ_ALL_BLOCKS == msg.type) {
                        out.writeObject(new AgentMessage.MessageBuilder()
                                .withSender(agent.getPort())
                                .withType(RSP_ALL_BLOCKS)
                                .withBlocks(agent.getBlocks())
                                .build());
                        break;
                    } else if (REQ_PEERS == msg.type) {
                        out.writeObject(new AgentMessage.MessageBuilder()
                                .withSender(agent.getPort())
                                .withSenderAgent(agent.getAddress() + ":" + agent.getPort())
                                .withType(RSP_PEERS)
                                .withPeers(agent.getPeers())
                                .build());
                        break;
                    } else if (NEW_AGENT == msg.type) {
                        agent.getAgentManager().addPeer(msg.senderAgent);
                        agent.broadcastNewPeer(msg.senderAgent);
                    } else if (BROADCAST_PEERS == msg.type) {
                        for (String p : msg.peers) {
                            agent.getAgentManager().addPeer(p);
                        }
                    }
                }
            }
            client.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
