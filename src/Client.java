
import java.security.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

public class Client extends PeerNode implements IClient {
    Map<String, KeyPair> keys;
    
    public Client(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName,ID);
        keys = new HashMap<>();
    }
    
    @Override
    public void readTransaction() {

    }

    @Override
    public void generateKeys(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

        for (TransactionInput input : inputs) {
            KeyPair key = null;
            if (transaction.getAllTransactionInput().size() == 1
                    && transaction.getAllTransactionInput().get(0).getPreviousTransaction().equals("0")) {
                key = KeyUtils.GenerateKeys();
            } else {
                key = keys.get(input.getOutputIndex());
            }
            input.setSignature(KeyUtils.generateSignature(key.getPrivate(), input.getOutputIndex()));
            input.setPayerPublicKey(KeyUtils.getPublicKeyString(key.getPublic()));
        }

        for (TransactionOutput output : outputs) {
            KeyPair key = KeyUtils.GenerateKeys();
            if (keys.containsKey(output.getIndex())) {
                key = keys.get(output.getIndex());
            } else {
                keys.put(output.getIndex(), key);
            }
            output.setPayeePublicKey(KeyUtils.getPublicKeyString(key.getPublic()));
        }
    }


    @Override
    public void broadcastTransaction() {

    }

    private static Transaction parseTransaction(String t){
        String[] params = t.split("\t");
        Transaction trans = new Transaction();
        //set id
        trans.setId(params[0]);
        // set input
        TransactionInput transactionInput = new TransactionInput();
        String[] prevtx = params[2].split(":");
        String[] opIndex = params[3].split(":");
        transactionInput.setPreviousTransaction(prevtx[1]);
        transactionInput.setOutputIndex(opIndex[1]);
        trans.addInput(transactionInput);
        //set output
        for (int i = 4; i < params.length; i+=2) {
            TransactionOutput transactionOutput = new TransactionOutput();
            String[] val = params[i].split(":");
            String[] output = params[i + 1].split(":");
            transactionOutput.setIndex(output[1]);
            transactionOutput.setValue(Float.parseFloat(val[1]));
            trans.addOutput(transactionOutput);
        }
        return trans;
    }
}
