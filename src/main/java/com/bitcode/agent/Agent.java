package com.bitcode.agent;

import com.bitcode.agent.message.NewBlockMessage;
import com.bitcode.agent.message.NewNode;
import com.bitcode.agent.message.SyncBlocksRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.keeptoken.p2p.peer.Config;
import com.keeptoken.p2p.peer.PeerHandle;
import com.keeptoken.p2p.peer.network.message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bitcode.agent.AgentMessage.MESSAGE_TYPE.*;

public class Agent {

    private String id;
    private String name;
    private String address;
    private int port;

    private ServerSocket serverSocket;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(10);

    private boolean listening = true;

    private BlockChain blockChain;

    private List<String> peers = new ArrayList<>();

    private AgentManager agentManager;

    PeerHandle peerHandle;

    private final ThreadFactory factory = new ThreadFactory() {
        private AtomicInteger cnt = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "sync-" + cnt.getAndIncrement());
        }
    };

    private Long randomSleep ;

    // for jackson
    public Agent() {
        id = UUID.randomUUID().toString();
    }

    Agent(final String name, final String address, final int port, final Block  root, AgentManager agentManager) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        randomSleep = Math.abs(random.nextLong() % 500) + 100;
        id = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.port = port;
        this.agentManager = agentManager;
        blockChain = new BlockChain(root);
        peers.addAll(agentManager.getDefaultPeerList());

        final Config config = new Config();
        config.setPeerName(name);

        peerHandle = new PeerHandle(this, config, port);
    }

    public void addPeer(String host, int port) {
        String id = host+":"+port;
        if(!peers.contains(id)) {
            peers.add(id);
        }
    }

    Block createBlock() {
        if (blockChain.isEmpty()) {
            return null;
        }

        Block previousBlock = getLatestBlock();
        if (previousBlock == null) {
            return null;
        }

        final int index = previousBlock.getIndex() + 1;
        final Block block = new Block(index, previousBlock.getHash(), name);
        System.out.println(String.format("%s created new block %s", name, block.toString()));
//        broadcast(INFO_NEW_BLOCK, block);
        addBlock(block);
        broadcast(new NewBlockMessage(new Block(block)));
        return block;
    }

    void addBlock(Block block) {
        if (isBlockValid(block)) {
            blockChain.add(block);
        }
    }

    void joinNetwork() {
        broadcast(NEW_AGENT, null);
    }

    void startHost() {

        try {
            peerHandle.start();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

//        executor.execute(() -> {
//            try {
//                serverSocket = new ServerSocket(port);
//                System.out.println(String.format("Server %s started", serverSocket.getLocalPort()));
//                listening = true;
//                while (listening) {
//                    final AgentServerThread thread = new AgentServerThread(Agent.this, serverSocket.accept());
//                    thread.start();
//                }
//                serverSocket.close();
//            } catch (IOException e) {
//                System.err.println("Could not listen to port " + port);
//            }
//        });
//        broadcast(NEW_AGENT, null);
//        broadcast(REQ_ALL_BLOCKS, null);



    }

    void stopHost() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void startMine() {
        executor.execute(() -> {
            try {
                while (true) {
                    Block block = createBlock();



                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        executor.execute(()-> {
            try {


                Thread.sleep(15000);
                for(String peer: peers) {
                    if (!getPeerId().equalsIgnoreCase(peer)) {
                        String[] args = peer.split(":");
                        String peerHost = args[0];
                        int peerPort = Integer.parseInt(args[1]);
                        peerHandle.connect(peerHost, peerPort);
                    }
                }

                broadcast(new NewNode(this.address, this.port));
                broadcast(new SyncBlocksRequest(1));

            }catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    @JsonIgnore
    public List<Block> getBlocks() {
        return blockChain.getBlocks();
    }

    public List<Block> splitBlocks(int from) {
        return blockChain.split(from);
    }

    public Block getLatestBlock() {
        if (blockChain.isEmpty()) {
            return null;
        }
        return blockChain.getLatestBlock();
    }

    public void syncBlocks(List<Block> blocks) {
        blockChain.add(blocks);
    }

    private boolean isBlockValid(final Block block) {
        final Block latestBlock = getLatestBlock();
        if (latestBlock == null) {
            return false;
        }
        final int expected = latestBlock.getIndex() + 1;
        if (block.getIndex() != expected) {
            System.out.println(String.format("Invalid index. Expected: %s Actual: %s", expected, block.getIndex()));
            return false;
        }
        if (!Objects.equals(block.getPreviousHash(), latestBlock.getHash())) {
            System.out.println("Unmatched hash code");
            return false;
        }
        return true;
    }

    public void broadcastNewPeer(final String address) {
        agentManager.getPeers().forEach(peer -> {
            String[] args = peer.split(":");
            String peerHost = args[0];
            int peerPort = Integer.parseInt(args[1]);
            if (!getPeerId().equalsIgnoreCase(peer)) {
                try (final Socket peerSock = new Socket(peerHost, peerPort);
                     final ObjectOutputStream out = new ObjectOutputStream(peerSock.getOutputStream());
                     final ObjectInputStream in = new ObjectInputStream(peerSock.getInputStream())) {

                    List<String> newPeers = Collections.singletonList(peer);
                    AgentMessage message = new AgentMessage.MessageBuilder().withSenderAgent(this.getAddress() + ":" + this.getPort()).withSender(this.getPort()).withType(BROADCAST_PEERS)
                            .withPeers(newPeers)
                            .build();
                    out.writeObject(message);
                } catch (UnknownHostException e) {
                    System.err.println(String.format("Unknown host %s %d", peerHost, peerPort));
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println(String.format("%s couldn't get I/O for the connection to %s. Retrying...%n", getPort(), port));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void broadcast(Message message) {
        peerHandle.broadcast(message);
    }

    public void broadcast(AgentMessage.MESSAGE_TYPE type, final Block block) {
        agentManager.getPeers().forEach(peer -> {
            String[] args = peer.split(":");
            String peerHost = args[0];
            int peerPort = Integer.parseInt(args[1]);
            if (!getPeerId().equalsIgnoreCase(peer)) {
                sendMessage(type, peerHost, peerPort, block);
            }
        });
    }



    private void sendMessage(AgentMessage.MESSAGE_TYPE type, String host, int port, Block... blocks) {
//        if (com.bitcode.agent.getId().equals(this.id)) {
//            return;
//        }
        System.out.println(String.format("send %s:%d message: %s", host, port, type));
        try (
                final Socket peer = new Socket(host, port);
                final ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(peer.getInputStream())) {
            if (type == NEW_AGENT) {
                AgentMessage message = new AgentMessage.MessageBuilder().withSenderAgent(this.getAddress() + ":" + this.getPort()).withSender(this.getPort()).withType(type)
                        .build();
                out.writeObject(message);
            }

            if (type == REQ_PEERS) {
                AgentMessage message = new AgentMessage.MessageBuilder().withSenderAgent(this.getAddress() + ":" + this.getPort()).withSender(this.getPort()).withType(type)
                        .withPeers(this.getPeers()).build();
                out.writeObject(message);
            }

            if(type == REQ_ALL_BLOCKS) {
                AgentMessage message = new AgentMessage.MessageBuilder().withSenderAgent(this.getAddress() + ":" + this.getPort()).withSender(this.getPort()).withType(type)
                        .build();
                out.writeObject(message);
            }

            Object fromPeer;
            while ((fromPeer = in.readObject()) != null) {
                if (fromPeer instanceof AgentMessage) {
                    final AgentMessage msg = (AgentMessage) fromPeer;
                    System.out.println(String.format("%d received: %s", this.port, msg.toString()));
                    if (READY == msg.type) {
                        out.writeObject(new AgentMessage.MessageBuilder()
                                .withType(type)
                                .withReceiver(port)
                                .withSender(this.port)
                                .withSenderAgent(address + ":" + this.port)
                                .withReceiverAgent(host + ":" + port)
                                .withBlocks(Arrays.asList(blocks)).build());
                    } else if (RSP_ALL_BLOCKS == msg.type) {
                        if (!msg.blocks.isEmpty() && this.blockChain.size() == 1) {
                            blockChain.add(msg.blocks);
                        }
                        break;
                    } else if (RSP_PEERS == msg.type) {
                        List<String> allPeers = msg.peers;
                        for (String p : allPeers) {
                            agentManager.addPeer(p);
                        }
                    }

                }
            }
        } catch (UnknownHostException e) {
            System.err.println(String.format("Unknown host %s %d", host, port));
        } catch (IOException e) {
            System.err.println(String.format("%s:%s couldn't get I/O for the connection to %s:%s. Retrying...%n", getAddress(), getPort(), host, port));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getPeers() {
        return agentManager.getPeers();
    }

    @JsonIgnore
    public AgentManager getAgentManager() {
        return agentManager;
    }

    public String getPeerId() {
        return address + ":" + port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
