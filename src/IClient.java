public interface IClient {
    void readTransaction(String filename);
    //private
    void generateKeys(Transaction transaction);
    void broadcastTransaction(String transaction);
}
