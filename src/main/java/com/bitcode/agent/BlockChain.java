package com.bitcode.agent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChain implements Serializable {

    private List<Block> blocks = new LinkedList<>();

    private Object lock = new Object();

    private int chainHeight;

    private Block chainHead;

    protected final ReentrantLock locking = new ReentrantLock();

    class BlockElement {
        Block block;
        int index;

        public BlockElement(Block block, int index) {
            this.block = block;
            this.index = index;
        }

        public Block getBlock() {
            return block;
        }

        public void setBlock(Block block) {
            this.block = block;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }
    }

    public BlockChain(Block root) {
        add(root);
    }

    public boolean addWithBlock(Block block) {
        locking.lock();
        try {
            return add(block);
        } finally {
            locking.unlock();
        }
    }

    public boolean add(Block block) {
//        synchronized (lock) {
//            blocks.add(block);
//        }
//        add(Collections.singletonList(block));


        int height = chainHeight;
        int blockHeight = block.getIndex();

        if (block.equals(chainHead)) {
            return false;
        }

        int willHeight;

        BlockElement element = getBlockInCurrentScope(block.getPreviousHash());

        Block prevBlock = null;

        if (element != null) {
            prevBlock = element.getBlock();
        }

        if (prevBlock != null) {
            willHeight = prevBlock.getIndex() + 1;
        } else {
            willHeight = -1;
        }

        if (prevBlock == null) {
            //未能找到，先放一边
        } else {
            Block headBlock = chainHead;
            if (prevBlock.equals(headBlock)) {
                setChainHead(block);
                blocks.add(block);
            } else {
                int idx = element.getIndex();
                blocks.subList(idx, blocks.size()).clear();
                setChainHead(block);
                blocks.add(block);
            }
        }


        return true;

    }

    private void setChainHead(Block newBlock) {
        chainHead = newBlock;
        chainHeight = newBlock.getIndex();
    }

    private BlockElement getBlockInCurrentScope(String hash) {
        int index = 0;
        for (Block old : blocks) {
            if (old.hash.equals(hash)) {
                return new BlockElement(old, index);
            }
            index++;
        }
        return null;
    }


    public void add(List<Block> blockList) {
        locking.lock();
        try {
            if (blockList.size() <= 0) return;
            for (Block block : blockList) {
                add(block);
            }
        } finally {
            locking.unlock();
        }
    }

    public void addx(List<Block> blockList) {
        synchronized (lock) {
            if (blockList.size() <= 0) return;
            Block willLatestBlock = blockList.get(blockList.size() - 1);
            for (Block block : blockList) {
                int find = 0;
                for (Block old : blocks) {
//                    if (old.getHash().equals(block.getHash())) {
                    if (old.getIndex() == block.getIndex()) {
                        if (old.getHash().equals(block.getHash()) && old.getPreviousHash().equals(block.getPreviousHash())) {
                            find = 1;
                        } else {
                            find = 2;
                        }
                        break;
                    }

                }
                if (find == 1) {
                    continue;
                } else if (find == 2) {
                    //相同高度，但hash不一致，
                    System.out.println("======!!!!!===== block height not unique" + block.getCreator() + ", index=" + block.getIndex() + " , " + block.getHash());
                    break;
                } else {
                    if (blocks.size() > 0) {
                        Block latest = blocks.get(blocks.size() - 1);
                        if (latest.getIndex() + 1 != block.getIndex()) {
                            System.out.println("!!!!! " + latest.getCreator() + " latest height != block.index" + latest.getIndex() + " != " + block.getIndex());
                        }
                        if (!latest.getHash().equals(block.getPreviousHash())) {
                            System.out.println("!!!!===== latest hash != block previous hash, " + latest.getHash() + " != " + block.getPreviousHash());

                        }

                        if (latest.getIndex() + 1 != block.getIndex() || !latest.getHash().equals(block.getPreviousHash())) {
                            //wrong
                            if (latest.getIndex() + 1 < willLatestBlock.getIndex()) {
                                //这一个latest需要去掉，或重新校验本地block TODO
                            }
                        }
                    }
                    blocks.add(block);
                }
            }
        }
    }

    public int findIndex(int from) {
        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getIndex() == from) {
                return i;
            }
        }
        return -1;
    }

    public Block findBlock(int from) {
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block.getIndex() == from) {
                return block;
            }
        }
        return null;
    }

    public List<Block> split(int from) {
        synchronized (lock) {
            int latest = getLatestBlock().getIndex();
            int fromIndex = findIndex(from);
            if (fromIndex < 0) {
                return new LinkedList<>();
            }
            List<Block> myblocks = new LinkedList<>();
            for (int i = fromIndex; i < blocks.size(); i++) {
                myblocks.add(new Block(blocks.get(i)));
            }

            return myblocks;
        }
    }

    public boolean isEmpty() {
        return blocks.isEmpty();
    }

    public Block getLatestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public int size() {
        return blocks.size();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Object getLock() {
        return lock;
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }
}
