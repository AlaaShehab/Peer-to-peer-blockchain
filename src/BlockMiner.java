import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockMiner implements Miner {

    private static final int SPENT = 0;
    private static final int AVAILABLE = 1;

    private Map<Pair<String, String>, Integer> spendings;
    private Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;

    public BlockMiner () {
        spendings = new HashMap<>();
        chain = new Blockchain(GensisBlock.getGensisBlock());
    }

    // Listen on ports for when a new block is broadcasted.
    // calls verifyBlock
    @Override
    public void receiveBlock(Block block) {

    }

    // private method called from receive
    // verify the received block then stop miner if valid block
    // call updateToBeMinedBlockTransaction to remove old transactions
    // call updateSpending on every incoming verified block
    private boolean verifyBlock(Block block) {
        return false;
    }

    // Broadcast block after it is mined.
    // update spendings before broadcasting the block
    @Override
    public void broadcastBlock(Block block) {

    }

    @Override
    public void mineBlock() {

    }
    // Listen on ports for when a transactions sent by Clients.
    // Calls verifyTransaction first then if true adds new transaction
    @Override
    public void receiveTransaction(Transaction transaction) {

    }

    // If needed to register a Miner.
    @Override
    public void addMiner() {

    }

    @Override
    public boolean verifyTransaction(Transaction transaction) {
        // verify sum
        // verify no double spending
        // verify signature
        return verifySum(transaction) && !doubleSpendings(transaction) && validSignature(transaction);
    }

    private boolean verifySum (Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

        float moneyReceived = 0;
        float moneySpent = 0;

        for (TransactionInput input : inputs) {
            Block previousTransBlock = chain.getTransactionBlock(input.getPreviousTransaction()).block;
            Transaction previousTrans = previousTransBlock == null ? null : previousTransBlock.getTransaction(input.getPreviousTransaction());
            moneyReceived += previousTrans == null ? 0 : previousTrans.getTransactionOutput(input.getOutputIndex()).getValue();
        }

        for (TransactionOutput output : outputs) {
            moneySpent += output.getValue();
        }

        return moneyReceived >= moneySpent;
    }

    private boolean doubleSpendings (Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        for (TransactionInput input : inputs) {
            Pair<String, String> coin = new Pair<>(input.getPreviousTransaction(), input.getOutputIndex());
            if (!spendings.containsKey(coin) || spendings.get(coin) == SPENT) {
                return true;
            }
        }
        return false;
    }

    private boolean validSignature (Transaction transaction) {
        return true;
    }
    private void updateSpendings (Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            List<TransactionInput> inputs = transaction.getAllTransactionInput();
            List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

            for (TransactionInput input : inputs) {
                Pair<String, String> coin = new Pair<>(input.getPreviousTransaction(), input.getOutputIndex());
                if (spendings.containsKey(coin) && spendings.get(coin) == AVAILABLE) {
                    spendings.replace(coin, SPENT);
                }
            }

            for (TransactionOutput output : outputs) {
                Pair<String, String> coin = new Pair<>(transaction.getId(), output.getIndex());
                spendings.put(coin, AVAILABLE);
            }
        }
    }

    private void updateToBeMinedBlockTransaction (Block newReceivedBlock) {
        List<Transaction> transactions = newReceivedBlock.getTransactions();
        for (Transaction transaction : transactions) {
            toBeMinedBlock.removeTransaction(transaction.getId());
        }
    }
}
