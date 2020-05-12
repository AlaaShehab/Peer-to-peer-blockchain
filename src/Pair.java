public class Pair {
    private String txID;
    private String outputIndex;

    public Pair (String txID, String outputIndex) {
        this.txID = txID;
        this.outputIndex = outputIndex;
    }

    public String getTxID() {
        return txID;
    }

    public void setTxID(String txID) {
        this.txID = txID;
    }

    public String getOutputIndex() {
        return outputIndex;
    }

    public void setOutputIndex(String outputIndex) {
        this.outputIndex = outputIndex;
    }
}
