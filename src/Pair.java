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

    @Override
    public boolean equals(Object pair) {
        return this.txID.equals(((Pair) pair).getTxID())
                && this.outputIndex.equals(((Pair) pair).getOutputIndex());
    }
    @Override
    public int hashCode() {
        return (txID + outputIndex).hashCode();
    }
}
