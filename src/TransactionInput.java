public class TransactionInput {
    private String previousTransaction = "";
    private String outputIndex = "";
    private String signature = "";
    private String payerPublicKey = "";

    public String getPreviousTransaction() {
        return previousTransaction;
    }

    public void setPreviousTransaction(String previousTransaction) {
        this.previousTransaction = previousTransaction;
    }

    public String getOutputIndex() {
        return outputIndex;
    }

    public void setOutputIndex(String outputIndex) {
        this.outputIndex = outputIndex;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPayerPublicKey() {
        return payerPublicKey;
    }

    public void setPayerPublicKey(String payerPublicKey) {
        this.payerPublicKey = payerPublicKey;
    }
}
