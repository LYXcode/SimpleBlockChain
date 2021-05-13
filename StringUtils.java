import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
    public static String applySha256(String input){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();
            for(int i = 0; i < hash.length; i++){
                //0xff & 为了保证byte类型转int后二进制保持一致 即补零扩展，int是32位 而byte是8位 比如byte的-12 是 1111 0100 直接补位（符号位扩展） 11111111 11111111 11111111 11110100转成十六进制字符串则是fffffff4 
                //而补零扩展则是00000000 00000000 00000000 11110100 转成十六进制字符串f4
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1){
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
}
