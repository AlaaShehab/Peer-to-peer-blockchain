import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyUtils {
    public static KeyPair GenerateKeys() {
        KeyPairGenerator g = null;
        try {
            g = KeyPairGenerator.getInstance("EC");
            g.initialize(256, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return g.generateKeyPair();
    }

    public static PublicKey getPublicKey(String hexKey) {
        byte[] encodedKey = Utils.toByte(hexKey);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encodedKey);
        try {
            return factory.generatePublic(encodedKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey(String hexKey) {
        byte[] encodedKey = Utils.toByte(hexKey);
        KeyFactory factory = null;
        try {
            factory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        try {
            return factory.generatePrivate(encodedKeySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateSignature(PrivateKey sk, String plaintext) {
        byte[] signature = null;
        try {
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            ecdsaSign.initSign(sk);
            ecdsaSign.update(plaintext.getBytes(StandardCharsets.UTF_8));
            signature = ecdsaSign.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return Utils.toHexString(signature);
    }

    public static boolean verifySignature (String publicKey, String plaintext, String sign) {
        PublicKey pk = getPublicKey(publicKey);
        boolean verify = false;
        try {
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(pk);
            signature.update(plaintext.getBytes());
            verify = signature.verify(Utils.toByte(sign));
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
        }
        return verify;
    }

    public static String getPublicKeyString (PublicKey pk) {
        return Utils.toHexString(pk.getEncoded());
    }

    public static String getPrivateKeyString (PrivateKey sk) {
        return Utils.toHexString(sk.getEncoded());
    }
}
