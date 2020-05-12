import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private int blockSize = 10;
    private String hash = "";
    private String merkleTreeRoot = "";
    private String previousBlockHash = "";
    private List<Transaction> transactions;
    private long timestamp;
    private int nonce = 0;

    public Block () {
        transactions = new ArrayList<>();
    }

    public String hash() {
        return hash;
    }

    public boolean verifyHash() {
        return hash.equals(calculateBlockHash());
    }

    //Hardness is the number of zeros in the beginning of the hash
    public void solve(int hardness) {
        do {
            nonce++;
            hash = calculateBlockHash();
        } while (!hash.substring(0, hardness).equals("0".repeat(hardness)));
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
            int start = 0;
            while (start < size) {
                String node = tree.remove(0);
                node += start + 1 >= size ? node : tree.remove(0);
                tree.add(Utils.toHexString(digest.digest(node.getBytes(StandardCharsets.UTF_8))));
                start += 2;
            }
        }
        return tree.get(0);
    }

    public String calculateBlockHash() {
        String input = previousBlockHash + merkleTreeRoot + Long.toString(timestamp) + Integer.toString(nonce);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hashedInput = Utils.toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
            return hashedInput;
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
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

    public boolean containsTransaction (String transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(transactionID)) {
                return true;
            }
        }
        return false;
    }

    public void removeTransaction (String transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(transactionID)) {
                transactions.remove(transaction);
                return;
            }
        }
    }

    public Transaction getTransaction (String transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(transactionID)) {
                return transaction;
            }
        }
        return null;
    }
}
