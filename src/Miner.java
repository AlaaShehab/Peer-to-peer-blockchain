import com.google.gson.Gson;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;

public class Miner extends PeerNode implements IMiner {
    private Set<Pair> spendings;
    private Blockchain chain;
    private Block toBeMinedBlock;
    private List<Transaction> incomingTransactions;
    private int hardness = 5; // example
    private Thread miningThread;

    public Miner(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName,ID,"miner");
        chain = new Blockchain(GensisBlock.getGensisBlock());
        spendings = new HashSet<>();
        incomingTransactions = new ArrayList<>();
    }

    @Override
    public void receiveBlock() {
        if (blockList.isEmpty()) {
            return;
        }
        System.out.println(blockList.get(0));
        Block block = buildBlock(blockList.remove(0));
        if (!block.verifyHash() || !chain.addBlock(block)) {
            return;
        }
        miningThread.interrupt();
        updateSpendings(block);
        updateToBeMinedBlockTransaction(block);
        restartMiningThread();
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
        System.out.println(toBeMinedBlock);
    }

    @Override
    public void mineBlock() {
        if (toBeMinedBlock == null) {
            toBeMinedBlock = new Block();
        }
    	long startTime = System.currentTimeMillis();
        int takenTransactions = 0;
        while(((System.currentTimeMillis() - startTime) < 20000)&&(takenTransactions < toBeMinedBlock.getBlockSize())){
            if (incomingTransactions.isEmpty()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println("Miner - mineBlock - Interrupted");
                }
                continue;
            }
            toBeMinedBlock.addTransaction(incomingTransactions.remove(0));
            System.out.println("Adding transaction to block - transaction list size : "
                    + toBeMinedBlock.getTransactions().size());
            takenTransactions++;
        }
        System.out.println("Mining block");
        toBeMinedBlock.setPreviousBlockHash(chain.getChainHead().block.hash());
        toBeMinedBlock.setMerkleTreeRoot(toBeMinedBlock.calculateMerkleTreeRoot());
        toBeMinedBlock.setTimestamp(startTime * 1000);
        toBeMinedBlock.setHash(toBeMinedBlock.calculateBlockHash());
        toBeMinedBlock.solve(hardness);
        System.out.println(toBeMinedBlock.hash());
        broadcastBlock();
    }

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
                System.out.println("Adding transaction to list : " + incomingTransactions.size());
            }
        }
    }

    @Override
    public boolean verifyTransaction(Transaction transaction) {
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
