public interface IMiner {

    void receiveBlock ();
    void broadcastBlock ();
    void mineBlock ();
    void receiveTransaction ();
    boolean verifyTransaction (Transaction transaction);
}
