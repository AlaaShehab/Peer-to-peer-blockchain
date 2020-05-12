import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;

public class Miner extends PeerNode implements IMiner {
    private Set<Pair<String, String>> spendings;
    private Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;
    private int hardness = 30; // example

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
    public void receiveBlock(String block) {
        //TODO build the block from the file
        Block b = new Block(); //dump block will be replaced once we build the new block
        if (!b.verifyHash() || !chain.addBlock(b)) {
            //TODO return false as this block is not added
            return;
        }
        updateSpendings(b);
        updateToBeMinedBlockTransaction(b);
        //TODO call mining again here or after method returns
    }

    // Broadcast block after it is mined.
    // update spendings before broadcasting the block
    @Override
    public void broadcastBlock(Block block) {

    }

    @Override
    public void mineBlock() {
        if (toBeMinedBlock == null) {
            toBeMinedBlock = new Block();
        }
    	long startTime = System.currentTimeMillis();
    	List<Transaction> acceptedTransactions = new ArrayList<>();
        int takenTransactions = 0;
        while(((System.currentTimeMillis() - startTime) < 10000)&&(takenTransactions < toBeMinedBlock.getBlockSize())){
        	acceptedTransactions.add(incomingTransactions.get(takenTransactions));
        }
        toBeMinedBlock.setPreviousBlockHash(chain.getChainHead().block.hash());
        toBeMinedBlock.setMerkleTreeRoot(toBeMinedBlock.calculateMerkleTreeRoot());
        toBeMinedBlock.setTransactions(acceptedTransactions);
        toBeMinedBlock.setTimestamp(startTime * 1000);
        toBeMinedBlock.setHash(toBeMinedBlock.calculateBlockHash());
        toBeMinedBlock.solve(hardness);
        broadcastBlock(toBeMinedBlock);
        //TODO set toBeMinedBlock = null after broadcasting is done
        //TODO update spendings after broadcasting is done
    }
    // Listen on ports for when a transactions sent by Clients.
    // Calls verifyTransaction first then if true adds new transaction
    @Override
    public void receiveTransaction(String trans) {
        Transaction transaction = buildTransaction(trans);
        boolean validTransaction = verifyTransaction(transaction);
        //TODO If not valid do something else
        if (validTransaction) {
            incomingTransactions.add(transaction);
        }
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

    private Transaction buildTransaction(String t) {
        String[] pairs = t.toString().split(",");
        Dictionary trans = new Hashtable();
        for (int i = 0; i < pairs.length; i++) {
            String[] keyValue = pairs[i].split("=");
            trans.put(keyValue[0], keyValue[1]);
        }
        Transaction transaction = new Transaction();
        transaction.setHasWitness(Boolean.parseBoolean((String) trans.get("hashWitness")));
        transaction.setWitnesses((List<Witness>) trans.get("hashWitness"));
        transaction.setId((String) trans.get("id"));

        transaction.setInputCounter(Integer.parseInt((String) trans.get("inputCounter")));
        for (int i = 0; i < transaction.getInputCounter(); i++) {
            TransactionInput transactionInput = new TransactionInput();
            transactionInput.setPreviousTransaction((String) trans.get("previousTransaction" + (i+1)));
            transactionInput.setOutputIndex((String) trans.get("outputIndex" + (i+1)));
            transactionInput.setSignature((String) trans.get("signature" + (i+1)));
            transactionInput.setPayerPublicKey((String) trans.get("payerPublicKey" + (i+1)));
        }
        transaction.setOutputCounter(Integer.parseInt((String) trans.get("outputCounter")));
        for (int i = 0; i < transaction.getOutputCounter(); i++) {
            TransactionOutput transactionOutput = new TransactionOutput();
            transactionOutput.setValue(Float.parseFloat((String) trans.get("value" + (i+1))));
            transactionOutput.setIndex((String) trans.get("output" + (i+1)));
            transactionOutput.setPayeePublicKey((String) trans.get("payeePublicKey" + (i+1)));
            transaction.addOutput(transactionOutput);
        }
        return transaction;
    }
}
