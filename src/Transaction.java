import java.util.ArrayList;
import java.util.List;

public class Transaction {

    private boolean hasWitness;
    private int inputCounter;
    private int outputCounter;
    private List<TransactionInput> input;
    private List<TransactionOutput> output;
    private List<Witness> witnesses;
    private String hash;
    private String id;

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

    public List<TransactionInput> getInput() {
        return input;
    }

    public void setInput(List<TransactionInput> input) {
        this.input = input;
    }

    public void addInput(TransactionInput input) {
        this.input.add(input);
    }

    public List<TransactionOutput> getOutput() {
        return output;
    }

    public void setOutput(List<TransactionOutput> output) {
        this.output = output;
    }

    public void addOutput (TransactionOutput output) {
        this.output.add(output);
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

    public boolean verifyInputOutputSum () {
        return false;
    }

    public String hash() {
        return hash;
    }

    public void calculateHash() {
    }

    public TransactionOutput getTransactionOutput (String outputIndex) {
        for (TransactionOutput transactionOutput : output) {
            if (transactionOutput.getIndex().equals(outputIndex)) {
                return transactionOutput;
            }
        }
        return null;
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
}
