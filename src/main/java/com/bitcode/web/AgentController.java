package com.bitcode.web;

import com.bitcode.agent.Agent;
import com.bitcode.agent.AgentManager;
import com.bitcode.agent.Block;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(path = "/agent")
public class AgentController {

    @Autowired
    private  AgentManager agentManager;

    @RequestMapping(method = GET)
    public Agent getAgent(@RequestParam("name") String name) {
        return agentManager.getAgent(name);
    }

    @RequestMapping(method = DELETE)
    public void deleteAgent(@RequestParam("name") String name) {
        agentManager.deleteAgent(name);
    }

    @RequestMapping(method = POST, params = {"name", "port"})
    public Agent addAgent(@RequestParam("name") String name, @RequestParam("port") int port) {
        return agentManager.addAgent(name, port);
    }

    @RequestMapping(path = "all", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getAllAgents(@RequestParam(value = "agent", defaultValue = "") String agentName) throws Exception{
        List<Agent> agents =  agentManager.getAllAgents();


        Map<String, Object> results = new HashMap<>();
        results.put("agents", agents);

        if (agents.size() > 0) {
            List<Block> blocks = null;
            if(agentName.equals("")) {
                blocks = agents.get(0).getBlocks();
            }else {
                Agent agent = agentManager.getAgent(agentName);
                if(agent == null) {
                    blocks = new ArrayList();
                }else {
                    blocks = agent.getBlocks();
                }
            }
            results.put("blocks", blocks);
        }else {

            results.put("blocks", new ArrayList());
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(results);
    }

    @RequestMapping(path = "all", method = DELETE)
    public void deleteAllAgents() {
        agentManager.deleteAllAgents();
    }

    @RequestMapping(method = POST, path = "mine")
    public Block createBlock(@RequestParam(value = "com/bitcode/agent") final String name) {
        return agentManager.createBlock(name);
    }
}