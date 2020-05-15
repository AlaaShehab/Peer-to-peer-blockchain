import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Cloneable {

    private boolean hasWitness;
    private int inputCounter;
    private int outputCounter;
    private List<TransactionInput> input;
    private List<TransactionOutput> output;
    private List<Witness> witnesses;
    private String hash;
    private String id;

    public Transaction () {
        input = new ArrayList<>();
        output = new ArrayList<>();
        witnesses = new ArrayList<>();
        hash = "";
        id = "";
    }
    public boolean isHasWitness() {
        return hasWitness;
    }

    public void setHasWitness(boolean hasWitness) {
        this.hasWitness = hasWitness;
    }

    public int getInputCounter() {
        return inputCounter;
    }

    public void setInputCounter(int inputCounter) {
        this.inputCounter = inputCounter;
    }

    public void increaseInputCounter () {
        inputCounter++;
    }

    public int getOutputCounter() {
        return outputCounter;
    }

    public void setOutputCounter(int outputCounter) {
        this.outputCounter = outputCounter;
    }

    public void increaseOutputCounter () {
        outputCounter++;
    }

    public void setInput(List<TransactionInput> input) {
        this.input = input;
    }

    public void addInput(TransactionInput input) {
        this.input.add(input);
        inputCounter++;
    }

    public List<TransactionOutput> getAllTransactionOutput() {
        return output;
    }

    public void setOutput(List<TransactionOutput> output) {
        this.output = output;
    }

    public void addOutput (TransactionOutput output) {
        this.output.add(output);
        outputCounter++;
    }

    public List<Witness> getWitnesses() {
        return witnesses;
    }

    public void setWitnesses(List<Witness> witnesses) {
        this.witnesses = witnesses;
    }

    public void addWitness (Witness witness) {
        this.witnesses.add(witness);
    }

    public boolean verifySignature () {
        return false;
    }

    public String hash() {
        return hash;
    }

    public void setHash (String hash) {
        this.hash = hash;
    }

    public String calculateHash() {
        Gson parser = new Gson();
        String toBeHashed = Integer.toString(inputCounter) + Integer.toString(outputCounter)
                + id + parser.toJson(input) + parser.toJson(output);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(toBeHashed.getBytes("UTF-8"));
            return Utils.toHexString(hash);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

    }

    public TransactionOutput getTransactionOutput (String outputIndex) {
        if (outputIndex == null) {
            return null;
        }
        try {
            Integer.parseInt(outputIndex);
        } catch (RuntimeException e) {
            return null;
        }
        return output.get(Integer.parseInt(outputIndex) - 1);
    }

    public List<TransactionInput> getAllTransactionInput () {
        return input;
    }

    public List<String> getAllTransactionInputIndices () {
        List<String> indices = new ArrayList<>();

        for (TransactionInput input : input) {
            indices.add(input.getPreviousTransaction());
        }
        return indices;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Transaction clone()throws CloneNotSupportedException{
        Transaction cloned = new Transaction();
        cloned.setId(this.id);
        cloned.setHasWitness(this.hasWitness);
        cloned.setHash(this.hash);
        for (int i = 0; i < inputCounter; i++) {
            cloned.addInput(input.get(i).clone());
        }
        for (int i = 0; i < outputCounter; i++) {
            cloned.addOutput(output.get(i).clone());
        }
        return cloned;
    }
}
