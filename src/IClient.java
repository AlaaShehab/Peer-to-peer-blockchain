public interface IClient {
    void readTransaction();
    //private
    void generateKeys(Transaction transaction);
    void broadcastTransaction();
}
