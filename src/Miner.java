public interface Miner {

    boolean verifyBlock(Block block);
    void broadcastBlock (Block block);
    void mineBlock ();
    void receiveTransaction (Transaction transaction);
    void getChainHead ();
    void addMiner ();
    boolean verifyTransaction (Transaction transaction);
}
