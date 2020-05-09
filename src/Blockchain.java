import java.util.LinkedList;
import java.util.List;

public class Blockchain {

    Block block;
    //Block chain is tree not just a list
    private List<Blockchain> chain = new LinkedList<>();

    public Blockchain (Block block) {
        this.block = block;
    }
    //TODO Alaa test addBlock
    public boolean addBlock (Block block) {
        if (this.block.hash().equals(block.getPreviousBlockHash())) {
            chain.add(new Blockchain(block));
            return true;
        }

        for (Blockchain child : chain) {
            if (child.addBlock(block)) {
                return true;
            }
        }
        return false;
    }
    //TODO Alaa test getTransactionBlock
    public Blockchain getTransactionBlock (String transactionID) {
        if (block.containsTransaction(transactionID)) {
            return this;
        }
        for (Blockchain child : chain) {
            if (child.getTransactionBlock(transactionID) != null) {
                return child;
            }
        }
        return null;
    }

    public Blockchain getChainHead () {
        if (chain.isEmpty()) {
            return this;
        }

        int max = 0;
        Blockchain current = null;
        for (Blockchain child : chain) {
            int depth = child.depth();
            if (depth > max) {
                max = depth;
                current = child.getChainHead();
            }
        }
        return current;
    }

    private int depth() {
        if (chain.isEmpty()) {
            return 1;
        }

        int max = 0;
        for (Blockchain child : chain) {
            int depth = child.depth();
            if (depth > max) {
                max = depth;
            }
        }
        return max + 1;
    }

}
