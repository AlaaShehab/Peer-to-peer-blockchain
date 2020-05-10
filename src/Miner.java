import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;

public class Miner extends PeerNode implements IMiner {
    private Set<Pair<String, String>> spendings;
    private Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;

    public Miner(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName,ID);
        spendings = new HashSet<>();
        chain = new Blockchain(GensisBlock.getGensisBlock());
        spendings = new HashSet<>();
        incomingTransactions = new ArrayList<>();
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

    // If needed to register a IMiner.
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
            if (initialTransaction(transaction)) {
                moneyReceived = Float.MAX_VALUE;
                break;
            }
            Transaction previousTrans = getTransaction(input.getPreviousTransaction());
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
            if (initialTransaction(transaction)) {
                return false;
            }
            Pair<String, String> coin = new Pair<>(input.getPreviousTransaction(), input.getOutputIndex());
            if (!spendings.contains(coin)) {
                return true;
            }
        }
        return false;
    }

    private boolean validSignature (Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();

        for (TransactionInput input : inputs) {
            if (initialTransaction(transaction)) {
                break;
            }
            Transaction previousTrans = getTransaction(input.getPreviousTransaction());
            if (previousTrans == null || previousTrans.getTransactionOutput(input.getOutputIndex()) == null) {
                continue;
            }
            if (!input.getPayerPublicKey().equals(previousTrans.getTransactionOutput(input.getOutputIndex()).getPayeePublicKey())) {
                return false;
            }
            if (!KeyUtils.verifySignature(input.getPayerPublicKey(), input.getOutputIndex(), input.getSignature())) {
                return false;
            }
        }
        return true;
    }

    private Transaction getTransaction (String TransactionID) {
        Block previousTransBlock = chain.getTransactionBlock(TransactionID).block;
        return previousTransBlock == null ? null : previousTransBlock.getTransaction(TransactionID);
    }

    private boolean initialTransaction (Transaction transaction) {
        return transaction.getAllTransactionInput().size() == 1
                && transaction.getAllTransactionInput().get(0).getPreviousTransaction().equals("0");
    }
    private void updateSpendings (Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            List<TransactionInput> inputs = transaction.getAllTransactionInput();
            List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

            for (TransactionInput input : inputs) {
                Pair<String, String> coin = new Pair<>(input.getPreviousTransaction(), input.getOutputIndex());
                if (spendings.contains(coin)) {
                    spendings.remove(coin);
                }
            }

            for (TransactionOutput output : outputs) {
                Pair<String, String> coin = new Pair<>(transaction.getId(), output.getIndex());
                spendings.add(coin);
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
