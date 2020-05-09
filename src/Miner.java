public interface Miner {

    void receiveBlock (Block block);
    //private method called from receive
    //verify stop miner if verified, remove old transactions from to-be mined block
    //start mining again
    boolean verifyBlock(Block block);
    void broadcastBlock (Block block);
    // wait until you get block transactions then start mining
    void mineBlock ();
    void receiveTransaction (Transaction transaction);
    void getChainHead ();
    void addMiner ();
    //double spending, sum input >= sum output, signature
    boolean verifyTransaction (Transaction transaction);
}
