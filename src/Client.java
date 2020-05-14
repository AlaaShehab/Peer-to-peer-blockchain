
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;

public class Client extends PeerNode implements IClient {
    Map<String, KeyPair> keys;
    
    public Client(int port,String hostName,int ID) throws InterruptedException, BrokenBarrierException {
    	super(port, hostName, ID,"client");
        keys = new HashMap<>();
    }

    @Override
    public void readTransaction(String filename) throws IOException {
    	broadcastTransaction("transaction"); //to start adding in txList
        File fileBasic = new File(filename + "Basic.txt");    //creates a new file instance
        FileReader frBasic = new FileReader(fileBasic);   //reads the file
        BufferedReader brBasic = new BufferedReader(frBasic);
        String line;
        while((line=brBasic.readLine())!=null)
        {
            Transaction transaction = parseBasicTransaction(line);
            generateKeys(transaction);
            Gson parser = new Gson();
            broadcastTransaction(parser.toJson(transaction));
        }
        frBasic.close();
        File file = new File(filename + ".txt");    //creates a new file instance
    	FileReader fr = new FileReader(file);   //reads the file
    	BufferedReader br = new BufferedReader(fr);
    	while((line=br.readLine())!=null)
    	{
			Transaction transaction = parseTransaction(line);
			generateKeys(transaction);
            Gson parser = new Gson();
            broadcastTransaction(parser.toJson(transaction));
    	}
    	fr.close();
    }

    @Override
    public void generateKeys(Transaction transaction) {
        List<TransactionInput> inputs = transaction.getAllTransactionInput();
        List<TransactionOutput> outputs = transaction.getAllTransactionOutput();

        for (TransactionInput input : inputs) {
            KeyPair key = null;
            if (transaction.getAllTransactionInput().size() == 1
                    && transaction.getAllTransactionInput().get(0).getOutputIndex().equals("0")) {
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
    public void broadcastTransaction(String transaction) {    	
    	Enumeration<Integer> e = minersPorts.elements();
    	while (e.hasMoreElements()) { 
    		client1.startConnection("127.0.0.1", e.nextElement());
			String msg1 = client1.sendMessage(transaction);            	
			client1.stopConnection();
    	}

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

    public static Transaction parseBasicTransaction(String t){
        String[] params = t.split("\t");
        Transaction trans = new Transaction();
        //set id
        trans.setId(params[0]);
        // set input
        TransactionInput transactionInput = new TransactionInput();
        String[] opIndex = params[1].split(":");
        transactionInput.setOutputIndex(opIndex[1]);
        transactionInput.setPreviousTransaction("0");
        trans.addInput(transactionInput);
        //set output
        TransactionOutput transactionOutput = new TransactionOutput();
        String[] value = params[2].split(":");
        String[] index = params[3].split(":");
        transactionOutput.setValue(Float.parseFloat(value[1]));
        transactionOutput.setIndex(index[1]);
        trans.addOutput(transactionOutput);
        return trans;
    }
}
