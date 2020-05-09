public interface Client {

    void readTransaction();
    //private
    void generateKeys();
    void generateSignature();
    void broadcastTransaction();
}
