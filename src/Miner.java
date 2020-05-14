import com.google.gson.Gson;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;

public class Miner extends PeerNode implements IMiner {
    private Set<Pair> spendings;
    private Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;
    private int hardness = 30; // example

    public Miner(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName,ID,"miner");
        chain = new Blockchain(GensisBlock.getGensisBlock());
        spendings = new HashSet<>();
        incomingTransactions = new ArrayList<>();
    }

    // Listen on ports for when a new block is broadcasted.
    // calls verifyBlock
    @Override
    public void receiveBlock(String receivedBlock) {
        Block block = buildBlock(receivedBlock);
        if (!block.verifyHash() || !chain.addBlock(block)) {
            return;
        }
        updateSpendings(block);
        updateToBeMinedBlockTransaction(block);
        //TODO call mining again here or after method returns
    }

    // Broadcast block after it is mined.
    // update spendings before broadcasting the block
    @Override
    public void broadcastBlock() {
        String toBroadcast = convertBlockToString(toBeMinedBlock);
        //sending block to all clients and miners except me.
        Enumeration<Integer> e = clientsPorts.elements();
    	while (e.hasMoreElements()) { 
    		client1.startConnection("127.0.0.1", e.nextElement());
    		String msg1 = client1.sendMessage("block"); 
			msg1 = client1.sendMessage(toBroadcast);            	
			client1.stopConnection();
    	}
    	@SuppressWarnings("unchecked")
		Hashtable<Integer, Integer> clone =  (Hashtable<Integer, Integer>) minersPorts.clone() ;
    	clone.remove(this.ID);
		Enumeration<Integer> e2 = clone.elements();
    	while (e2.hasMoreElements()) { 
    		client1.startConnection("127.0.0.1", e2.nextElement());
    		String msg1 = client1.sendMessage("block"); 
			msg1 = client1.sendMessage(toBroadcast);            	
			client1.stopConnection();
    	}
    	
        updateSpendings(toBeMinedBlock);
        toBeMinedBlock = null;
    }

    @Override
    public void mineBlock() {
        if (toBeMinedBlock == null) {
            toBeMinedBlock = new Block();
        }
    	long startTime = System.currentTimeMillis();
    	List<Transaction> acceptedTransactions = new ArrayList<>();
        int takenTransactions = 0;
        while(((System.currentTimeMillis() - startTime) < 60000)&&(takenTransactions < toBeMinedBlock.getBlockSize())){
            if (incomingTransactions.isEmpty()) {
                continue;
            }
            acceptedTransactions.add(incomingTransactions.get(0));
            incomingTransactions.remove(0);
            takenTransactions ++;
        }
        toBeMinedBlock.setPreviousBlockHash(chain.getChainHead().block.hash());
        toBeMinedBlock.setMerkleTreeRoot(toBeMinedBlock.calculateMerkleTreeRoot());
        toBeMinedBlock.setTransactions(acceptedTransactions);
        toBeMinedBlock.setTimestamp(startTime * 1000);
        toBeMinedBlock.setHash(toBeMinedBlock.calculateBlockHash());
        toBeMinedBlock.solve(hardness);
        broadcastBlock();
    }
    // Listen on ports for when a transactions sent by Clients.
    // Calls verifyTransaction first then if true adds new transaction
    @Override
    public void receiveTransaction() {
        while (!txList.isEmpty()) {
            if (txList.get(0).equals("transaction")) {
                txList.remove(0);
                continue;
            }
            Transaction transaction = buildTransaction(txList.remove(0));
            boolean validTransaction = verifyTransaction(transaction);
            if (validTransaction) {
                incomingTransactions.add(transaction);
            }
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
            Pair coin = new Pair(input.getPreviousTransaction(), input.getOutputIndex());
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
                && transaction.getAllTransactionInput().get(0).getOutputIndex().equals("0");
    }
    private void updateSpendings (Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            List<TransactionInput> inputs = transaction.getAllTransactionInput();
            List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

            for (TransactionInput input : inputs) {
                Pair coin = new Pair(input.getPreviousTransaction(), input.getOutputIndex());
                if (spendings.contains(coin)) {
                    spendings.remove(coin);
                }
            }

            for (TransactionOutput output : outputs) {
                Pair coin = new Pair(transaction.getId(), output.getIndex());
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

    public String convertBlockToString (Block block) {
        Gson parser = new Gson();
        return parser.toJson(block);
    }

    public Block buildBlock (String block) {
        Gson parser = new Gson();
        return parser.fromJson(block, Block.class);
    }

    public Transaction buildTransaction_ (String transaction) {
        Gson parser = new Gson();
        return parser.fromJson(transaction, Transaction.class);
    }

    public boolean isReceivedStringBlock (String received) {
        Gson parser = new Gson();
        Block block = parser.fromJson(received, Block.class);
        return !block.hash().isEmpty();
    }

    public boolean isReceivedStringTransaction (String received) {
        Gson parser = new Gson();
        Transaction transaction = parser.fromJson(received, Transaction.class);
        return !transaction.getId().isEmpty();
    }
}
