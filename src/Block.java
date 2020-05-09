import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    public void setMerkleTreeRoot(String merkleTreeRoot) {
        this.merkleTreeRoot = merkleTreeRoot;
    }
    //TODO Alaa test merkle tree root
    public String calculateMerkleTreeRoot() {
        ArrayList<String> tree = new ArrayList<>();
        for (Transaction t : transactions) {
            tree.add(t.hash());
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        while (tree.size() > 1) {
            int size = tree.size();
            for (int i = 0; i < size; i += 2) {
                String node = tree.remove(i);
                node += i + 1 == size ? node : tree.remove(i+1);
                try {
                    tree.add(Utils.toHexString(digest.digest(node.getBytes("UTF-8"))));
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        return tree.get(0);
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
