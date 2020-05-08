public class TransactionOutput {
    private float value;
    private int index;
    private String payeePublicKey;

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPayeePublicKey() {
        return payeePublicKey;
    }

    public void setPayeePublicKey(String payeePublicKey) {
        this.payeePublicKey = payeePublicKey;
    }
}
