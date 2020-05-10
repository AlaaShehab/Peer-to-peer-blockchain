public class Utils {
    public static String toHexString (byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
    public static byte[] toByte (String hexString) {
        byte[] byteArray = new byte[hexString.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(hexString.substring(index, index + 2), 16);
            byteArray[i] = (byte) j;
        }
        return byteArray;
    }
}
