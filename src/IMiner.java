public interface IMiner {

    void receiveBlock (String block);
    void broadcastBlock ();
    void mineBlock ();
    void receiveTransaction ();
    boolean verifyTransaction (Transaction transaction);
}
