public class TransactionOutput implements Cloneable{
    private double value;
    private String index = "";
    private String payeePublicKey = "";

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getPayeePublicKey() {
        return payeePublicKey;
    }

    public void setPayeePublicKey(String payeePublicKey) {
        this.payeePublicKey = payeePublicKey;
    }

    @Override
    public TransactionOutput clone()throws CloneNotSupportedException{
        TransactionOutput cloned = new TransactionOutput();
        cloned.setPayeePublicKey(this.payeePublicKey);
        cloned.setIndex(this.index);
        cloned.setValue(this.value);
        return cloned;
    }
}
