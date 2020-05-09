import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class BlockMiner implements Miner {

    Map<Pair<String, String>, Integer> spendings;
    Blockchain chain;
    public BlockMiner () {
        spendings = new HashMap<>();
        chain = new Blockchain(GensisBlock.getGensisBlock());
    }

    @Override
    public void receiveBlock(Block block) {

    }

    @Override
    public boolean verifyBlock(Block block) {
        return false;
    }

    @Override
    public void broadcastBlock(Block block) {

    }

    @Override
    public void mineBlock() {

    }

    @Override
    public void receiveTransaction(Transaction transaction) {

    }

    @Override
    public void getChainHead() {

    }

    @Override
    public void addMiner() {

    }

    @Override
    public boolean verifyTransaction(Transaction transaction) {
        return false;
    }
}
