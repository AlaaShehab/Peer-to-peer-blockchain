public class TransactionInput implements Cloneable{
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

    @Override
    public TransactionInput clone()throws CloneNotSupportedException{
        TransactionInput cloned = new TransactionInput();
        cloned.setSignature(this.signature);
        cloned.setOutputIndex(this.outputIndex);
        cloned.setPreviousTransaction(this.previousTransaction);
        cloned.setPayerPublicKey(this.payerPublicKey);
        return cloned;
    }
}
