import java.security.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client implements IClient {
    Map<String, KeyPair> keys;

    public Client () {
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
}
