public class GensisBlock {
    static Block gensis;
    static Block getGensisBlock () {
        if (gensis != null) {
            return gensis;
        }
        gensis = new Block();
        gensis.setTimestamp(System.currentTimeMillis() / 1000);
        gensis.setPreviousBlockHash("000");
        gensis.setHash(gensis.calculateBlockHash());
        return gensis;
    }
}
