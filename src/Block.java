import java.util.List;

public class Block {

    private int blockSize = 10;
    private String hash = "";
    private String merkleTreeRoot = "";
    private String previousBlockHash;
    private List<Transaction> transactions;
    private long timestamp;
    private int nonce;

    public String hash() {
        return hash;
    }

    public boolean verifyHash() {
        //TODO verify hash = calculatedHash
        return false;
    }

    public void solve() {
        //TODO do the mining for the block
    }

    @Override
    public String toString() {
       return "";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getMerkleTreeRoot() {
        return merkleTreeRoot;
    }

    public void calculateMerkleTreeRoot() {
        //TODO calculate merkle tree root
    }

    public void calculateBlockHash() {
        //TODO calculate hash using sha256
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
