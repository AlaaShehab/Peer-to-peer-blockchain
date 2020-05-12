public interface IMiner {

    void receiveBlock (String block);
    void broadcastBlock ();
    void mineBlock ();
    void receiveTransaction (String transaction);
    boolean verifyTransaction (Transaction transaction);
}
