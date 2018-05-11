package com.bitcode.agent;

import java.io.Serializable;
import java.util.List;

public class AgentMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    String senderAgent;
    String receiverAgent;
    int sender;
    int receiver;
    MESSAGE_TYPE type;
    List<Block> blocks;
    List<String> peers;

    public enum MESSAGE_TYPE {
        READY, INFO_NEW_BLOCK, REQ_ALL_BLOCKS, RSP_ALL_BLOCKS, REQ_PEERS, RSP_PEERS, NEW_AGENT, BROADCAST_PEERS
    }

    @Override
    public String toString() {
        return String.format("AgentMessage {type=%s, sender=%d, receiver=%d, blocks=%s}", type, sender, receiver, blocks);
    }

    static class MessageBuilder {
        private final AgentMessage message = new AgentMessage();

        MessageBuilder withSender(final int sender) {
            message.sender = sender;
            return this;
        }

        MessageBuilder withReceiver(final int receiver) {
            message.receiver = receiver;
            return this;
        }

        MessageBuilder withSenderAgent(final String sender) {
            message.senderAgent = sender;
            return this;
        }

        MessageBuilder withReceiverAgent(final String receiver) {
            message.receiverAgent = receiver;
            return this;
        }

        MessageBuilder withType(final MESSAGE_TYPE type) {
            message.type = type;
            return this;
        }

        MessageBuilder withBlocks(final List<Block> blocks) {
            message.blocks = blocks;
            return this;
        }

        MessageBuilder withPeers(final List<String> peers) {
            message.peers = peers;
            return this;
        }

        AgentMessage build() {
            return message;
        }

    }
}
