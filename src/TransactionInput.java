public class TransactionInput {
    private String previousTransaction;
    private int outputIndex;
    private String signature;

    public String getPreviousTransaction() {
        return previousTransaction;
    }

    public void setPreviousTransaction(String previousTransaction) {
        this.previousTransaction = previousTransaction;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    public void setOutputIndex(int outputIndex) {
        this.outputIndex = outputIndex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
