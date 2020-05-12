import java.io.IOException;

public interface IClient {
    void readTransaction(String filename) throws IOException;
    //private
    void generateKeys(Transaction transaction);
    void broadcastTransaction(String transaction);
}
