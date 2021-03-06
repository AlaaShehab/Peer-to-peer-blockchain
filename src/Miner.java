import com.google.gson.Gson;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

public class Miner extends PeerNode implements IMiner {
    private Set<Pair> spendings;
    Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;
    private int hardness = 5; // example
    private Thread miningThread;
    private boolean broadcasting;
    private Log bftLog= new Log();

    /**
	 * @return the bftLog
	 */
	public Log getBftLog() {
		return bftLog;
	}
	public Miner(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName,ID,"miner");
        chain = new Blockchain(GensisBlock.getGensisBlock());
        spendings = new HashSet<>();
        incomingTransactions = new ArrayList<>();
    }
    @Override
    public void receiveBFTBlock(){
    	if (blockList.isEmpty()) {
            return;
        }
        Block block = buildBlock(blockList.remove(0));
        if (!block.verifyHash() || !chain.addBlock(block)) {
            return;
        }
        if(block.getState()==Utils.states.preprapare){
        	block.setState(Utils.states.prepare);
        	bftLog.addPrepareRequest(block);
        	broadcastBlock(block);
        	
        	
        }else if(block.getState()==Utils.states.prepare){
        	bftLog.addPrepareRequest(block);
        	Runnable r = (Runnable) new MyRunnable(block,this,0);
        	new Thread(r).start();
        	//wait on count =2f

        }else if(block.getState()==Utils.states.commit){
        	bftLog.addCommitRequest(block);
        	Runnable r = (Runnable) new MyRunnable(block,this,1);
        	new Thread(r).start();
        	//wait on count =2f+1

        }
    }

    @Override
    public void receiveBlock() {
        if (blockList.isEmpty()) {
            return;
        }
        Block block = buildBlock(blockList.remove(0));
        if (!block.verifyHash() || !chain.addBlock(block)) {
            return;
        }
        while (broadcasting);
        System.out.println("Thread " +  Thread.currentThread().getId() + " : Received valid Block");
        System.out.println("Stopping thread : " + miningThread.getId());
        miningThread.interrupt();
        updateSpendings(block);
        updateToBeMinedBlockTransaction(block);
        System.out.println("Starting thread : " + miningThread.getId());
    }

    public void restartMiningThread() {
        miningThread = new Thread(() -> {
            while (true) {
                mineBlock();
            }
        });
        miningThread.start();
    }

    @Override
    public void broadcastBlock(Block b) {
        broadcasting = true;
        String toBroadcast = convertBlockToString(b);
        //sending block to all clients and miners except me.
        Enumeration<Integer> e = clientsPorts.elements();
    	while (e.hasMoreElements()) { 
    		client1.startConnection("127.0.0.1", e.nextElement());
    		String msg1 = client1.sendMessage(toBroadcast); 
    		messagesCount++;
			client1.stopConnection();
    	}
    	@SuppressWarnings("unchecked")
		Hashtable<Integer, Integer> clone =  (Hashtable<Integer, Integer>) minersPorts.clone() ;
    	clone.remove(this.ID);
		Enumeration<Integer> e2 = clone.elements();
    	while (e2.hasMoreElements()) { 
    		client1.startConnection("127.0.0.1", e2.nextElement());
    		String msg1 = client1.sendMessage(toBroadcast); 
    		messagesCount++;
			client1.stopConnection();
    	}
        toBeMinedBlock = null;
        System.out.println(toBeMinedBlock);
        broadcasting = false;
    }

    @Override
    public void mineBlock() {
        if (toBeMinedBlock == null) {
            toBeMinedBlock = new Block();
        }
        try {
            startMining();
        } catch (InterruptedException e) {
            System.out.println("Thread " +  Thread.currentThread().getId()
                    + " - mineBlock - Interrupted");
            try {
                TimeUnit.MILLISECONDS.sleep(1);
                return;
            } catch (InterruptedException ex) {
            }
        }
        broadcasting = true;
        try {
            chain.addBlock(toBeMinedBlock.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        broadcastBlock(toBeMinedBlock);
        System.out.println("Messages exchanged for a block: "+ messagesCount);
        messagesCountList.add(messagesCount);
        messagesCount=0;
    }

    private void startMining () throws InterruptedException {
        long startTime = System.currentTimeMillis();
        while(((System.currentTimeMillis() - startTime) < 300000
                && toBeMinedBlock.getTransactions().size() < toBeMinedBlock.getBlockSize())
                || toBeMinedBlock.getTransactions().size() == 0){
            if (incomingTransactions.isEmpty()) {
                TimeUnit.MICROSECONDS.sleep(1);
                continue;
            }
            toBeMinedBlock.addTransaction(incomingTransactions.remove(0));
            System.out.println("Thread " +  Thread.currentThread().getId()  + " Adding transaction to Block size : "
                    + toBeMinedBlock.getTransactions().size());
        }
        System.out.println("Thread " + Thread.currentThread().getId() +" : Mining block");
        //toBeMinedBlock.setPreviousBlockHash(chain.getLeastTimestampChain(chain.depth()).block.hash());
        toBeMinedBlock.setPreviousBlockHash(chain.getChainHead().block.hash());
        toBeMinedBlock.setMerkleTreeRoot(toBeMinedBlock.calculateMerkleTreeRoot());
        toBeMinedBlock.setTimestamp(startTime * 1000);
        toBeMinedBlock.setHash(toBeMinedBlock.calculateBlockHash());
        toBeMinedBlock.solve(hardness);
    }

    @Override
    public void receiveTransaction() {
        while (!txList.isEmpty()) {
            if (txList.get(0).equals("transaction")) {
                txList.remove(0);
                continue;
            }
            messagesCount++;
            Transaction transaction = buildTransaction(txList.remove(0));
            boolean validTransaction = verifyTransaction(transaction);
            if (validTransaction) {
                incomingTransactions.add(transaction);
                addTransactionSpendings(transaction);
                System.out.println("Thread " +  Thread.currentThread().getId()  + " : Adding transaction to list : "
                        + incomingTransactions.size());
            } else {
                System.out.println("Invalid Transaction");
            }
        }
    }

    @Override
    public boolean verifyTransaction(Transaction transaction) {
        return verifySum(transaction) && validSignature(transaction) && !doubleSpendings(transaction);
    }

    private boolean verifySum (Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

        double moneyReceived = 0;
        double moneySpent = 0;

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
        Blockchain currentChain = chain.getTransactionBlock(TransactionID);
        Block previousTransBlock = currentChain == null ? toBeMinedBlock : currentChain.block;
        Transaction transaction = previousTransBlock.containsTransaction(TransactionID)
                                    ? previousTransBlock.getTransaction(TransactionID)
                                    : getTransactionFromIncoming(TransactionID);
        return transaction;
    }

    private Transaction getTransactionFromIncoming (String transactionID) {
        for (Transaction transaction : incomingTransactions) {
            if (transaction.getId().equals(transactionID)) {
                return transaction;
            }
        }
        return null;
    }
    private boolean initialTransaction (Transaction transaction) {
        return transaction.getAllTransactionInput().size() == 1
                && transaction.getAllTransactionInput().get(0).getOutputIndex().equals("0");
    }
    private void addTransactionSpendings (Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

        for (TransactionInput input : inputs) {
            Pair coin = new Pair(input.getPreviousTransaction(), input.getOutputIndex());
            if (spendings.contains(coin)) {
                spendings.remove(coin);
            }
        }

        for (int i = 1; i <= outputs.size(); i++) {
            Pair coin = new Pair(transaction.getId(), String.valueOf(i));
            spendings.add(coin);
        }
    }
    private void updateSpendings (Block block) {
        List<Transaction> transactions = block.getTransactions();
        for (Transaction transaction : transactions) {
            if (toBeMinedBlock.containsTransaction(transaction.getId())
                || alreadyReceivedTransation(transaction.getId())) {
                continue;
            }
            addTransactionSpendings(transaction);
        }
    }

    private boolean alreadyReceivedTransation (String transactionID) {
        for (Transaction receivedTransaction : incomingTransactions) {
            if (receivedTransaction.getId().equals(transactionID)) {
                return true;
            }
        }
        return false;
    }

    private void updateToBeMinedBlockTransaction (Block newReceivedBlock) {
        List<Transaction> transactions = newReceivedBlock.getTransactions();
        for (Transaction transaction : transactions) {
            toBeMinedBlock.removeTransaction(transaction.getId());
        }
    }

    public String convertBlockToString (Block block) {
        Gson parser = new Gson();
        return parser.toJson(block);
    }

    public Block buildBlock (String block) {
        Gson parser = new Gson();
        return parser.fromJson(block, Block.class);
    }

    public Transaction buildTransaction (String transaction) {
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

