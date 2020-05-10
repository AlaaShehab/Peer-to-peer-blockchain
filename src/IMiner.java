public interface IMiner {

    void receiveBlock (Block block);
    void broadcastBlock (Block block);
    // wait until you get block transactions then start mining
    void mineBlock ();
    void receiveTransaction (Transaction transaction);
    void addMiner ();
    //double spending, sum input >= sum output, signature
    boolean verifyTransaction (Transaction transaction);
}
