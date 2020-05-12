public interface IMiner {

    void receiveBlock (String block);
    void broadcastBlock (Block block);
    // wait until you get block transactions then start mining
    void mineBlock ();
    void receiveTransaction (String transaction);
    //double spending, sum input >= sum output, signature
    boolean verifyTransaction (Transaction transaction);
}
