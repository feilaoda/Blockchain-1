package com.bitcode.agent;

import com.keeptoken.p2p.peer.Config;
import com.keeptoken.p2p.peer.PeerHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Component
public class AgentManager {

    private List<Agent> agents = new ArrayList<>();

    private List<String> peers = new ArrayList<>();

    private List<String> defaultPeerList = new ArrayList<>();

    @Value("${agent.name}")
    private String agentName;

    @Value("${agent.port}")
    private int agentPort;

    @Value("${agent.ip}")
    private String agentIp;

    @Value("${agent.defaultPeers}")
    private String defaultPeers;

    @Autowired
    private Genesis genesis;


    @PostConstruct
    public void load() {
        String agentPeer = agentIp+":"+agentPort;


        String[] ps = defaultPeers.split(",");
        for(int i=0; i<ps.length; i++) {
            String peer = ps[i];
            defaultPeerList.add(ps[i]);
        }

        final Config config = new Config();
        config.setPeerName(agentName);


//        if(!defaultPeerList.contains(agentPeer)) {
//            for(String peer: defaultPeerList) {
//                String[] hostports = peer.split(":");
//                if(hostports.length==2) {
//                    peerHandle.connect(hostports[0], Integer.parseInt(hostports[1]));
//                }
//            }
//        }


        Agent agent = new Agent(agentName, agentIp, agentPort, genesis, this);
        agent.startHost();
        agent.startMine();

        agents.add(agent);
    }

    public Agent addAgent(String name, int port) {


        Agent a = new Agent(name, "127.0.0.1", port, genesis, this);
        a.startHost();
        a.startMine();
        agents.add(a);

        return a;
    }

    public Agent getAgent(String name) {
        for (Agent a : agents) {
            if (a.getName().equals(name)) {
                return a;
            }
        }
        return null;
    }

    public List<Agent> getAllAgents() {
        return agents;
    }

    public void deleteAgent(String name) {
        final Agent a = getAgent(name);
        if (a != null) {
            a.stopHost();
            agents.remove(a);
        }
    }

    public List<Block> getAgentBlockchain(String name) {
        final Agent agent = getAgent(name);
        if (agent != null) {
            return agent.getBlocks();
        }
        return null;
    }

    public void deleteAllAgents() {
        for (Agent a : agents) {
            a.stopHost();
        }
        agents.clear();
    }

    public Block createBlock(final String name) {
        final Agent agent = getAgent(name);
        if (agent != null) {
            return agent.createBlock();
        }
        return null;
    }


    public void addPeer(String peer) {
        if (!peers.contains(peer)) {
            peers.add(peer);
        }
    }

    public List<String> getPeers() {
        return peers;
    }

    public List<String> getDefaultPeerList() {
        return defaultPeerList;
    }

    public void setDefaultPeerList(List<String> defaultPeerList) {
        this.defaultPeerList = defaultPeerList;
    }
}
