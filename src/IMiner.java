public interface IMiner {

    void receiveBlock ();
    void receiveBFTBlock();
    void broadcastBlock (Block b);
    void mineBlock ();
    void receiveTransaction ();
    boolean verifyTransaction (Transaction transaction);
}
