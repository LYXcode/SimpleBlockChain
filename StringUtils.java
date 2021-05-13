import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtils {
    public static String applySha256(String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                // 0xff & 为了保证byte类型转int后二进制保持一致 即补零扩展，int是32位 而byte是8位 比如byte的-12 是 1111 0100
                // 直接补位（符号位扩展） 11111111 11111111 11111111 11110100转成十六进制字符串则是fffffff4
                // 而补零扩展则是00000000 00000000 00000000 11110100 转成十六进制字符串f4
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');

                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

    }

    public static String getMerkleRoot(ArrayList<Transaction> transactions) {
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.transactionId);
        }

        ArrayList<String> treeLayer = previousTreeLayer;
//计算merkle tree hash值树，一个block可能有1000s的transaction 太多了以至于不能被包含在hash计算里，所以我们使用Merkel tree的根
        while (count > 1) {
            treeLayer = new ArrayList<String>();
            for (int i = 1; i < previousTreeLayer.size(); i++) {
                treeLayer.add(applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        String merkleRoot = (treeLayer.size() == 1)?treeLayer.get(0):"";
        return merkleRoot;
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String inpput) {
        Signature dsa;
        byte[] output = new byte[0];

        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = inpput.getBytes();
            dsa.update(strByte);
            byte[] realSign = dsa.sign();
            output = realSign;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }

        return output;

    }

    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }
}
