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

    //Hardness is the number of zeros in the beginning of the hash
    public void solve(int hardness) {
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

    public String calculateBlockHash() {
        //TODO calculate hash using sha256
    }

    public void setHash (String hash) {
        this.hash = hash;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
}
