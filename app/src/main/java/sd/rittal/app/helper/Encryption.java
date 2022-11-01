package sd.rittal.app.helper;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Encryption {

	    private static String cryptoPass = "sup3rS3xy";

    public static String Encrypt(String value) {
    	try {
    		DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    		SecretKey key = keyFactory.generateSecret(keySpec);

    		
    		// Cipher is not thread safe
    		Cipher cipher = Cipher.getInstance("DES");
    		cipher.init(Cipher.ENCRYPT_MODE, key);

    		
    		byte[] encryptedVal = cipher.doFinal(value.getBytes("UTF-8"));
            
    	       return Base64.encodeToString(encryptedVal, Base64.DEFAULT);


    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return value;
    };
    public static String Decrypt(String value) {
    	try {
    		DESKeySpec keySpec = new DESKeySpec(cryptoPass.getBytes("UTF8"));
    		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
    		SecretKey key = keyFactory.generateSecret(keySpec);
    		
    		byte[] encrypedPwdBytes = Base64.decode(value, Base64.DEFAULT);
    		// cipher is not thread safe
    		Cipher cipher = Cipher.getInstance("DES");
    		cipher.init(Cipher.DECRYPT_MODE, key);
    		byte[] decrypedValueBytes = cipher.doFinal(encrypedPwdBytes);
    		//ظظLog.d(TAG, "Decrypted: " + value + " -> " + decrypedValue);
    		return new String(decrypedValueBytes);


    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return value;
    } 
    

    
    
    
}
  











/*



import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.*;
import javax.crypto.spec.*;

import android.inputmethodservice.Keyboard.Key;
import android.util.Base64;

public class Encryption {

	static SecretKeySpec sks = null;
public static String  enc (String jj ){
	
	  // Original text
    String theTestText = jj;
  
   
     sks = null;
    try {
        
        sks = new SecretKeySpec(Param.SKEY.getBytes(), "AES");
    } catch (Exception e) {
        
    }

    // Encode the original data with AES
    byte[] encodedBytes = null;
    try {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, sks);
        System.out.print(Cipher.ENCRYPT_MODE);
        encodedBytes = c.doFinal(theTestText.getBytes());
    } catch (Exception e) {
       
    }
   
    return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
    
}
private static Cipher getCipher(int mode) throws Exception {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");

    //a random Init. Vector. just for testing
    byte[] iv = "e675f725e675f725".getBytes("UTF-8");

    c.init(mode, generateKey(), new IvParameterSpec(iv));
    return c;
}
    public static String  dec (String jj ){
    	//        Base64.encodeToString(encodedBytes, Base64.DEFAULT) + "\n");

byte[] kk = Base64.decode(jj, Base64.DEFAULT);
    	sks = null;
        try {
            
            sks = new SecretKeySpec(Param.SKEY.getBytes(), "AES");
        } catch (Exception e) {
            
        }
    // Decode the encoded data with AES
    byte[] decodedBytes = null;
    try {
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, sks);
        decodedBytes = c.doFinal(kk);
    } catch (Exception e) {
       
    }
   
   return (new String(decodedBytes));
}
    public static String Encrypt(String raw) throws Exception {
        Cipher c = getCipher(Cipher.ENCRYPT_MODE);

        byte[] encryptedVal = c.doFinal(raw.getBytes("UTF-8"));
        
       return Base64.encodeToString(encryptedVal, Base64.DEFAULT);
       
    }
    
    public static String Decrypt(String encrypted) throws Exception {

        byte[] decodedValue = Base64.decode(encrypted, Base64.DEFAULT);

        Cipher c = getCipher(Cipher.DECRYPT_MODE);
        byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }
    
    private static SecretKeySpec generateKey() throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        char[] password = "MO*&Hhg2_r".toCharArray();
        byte[] salt = "S@1tS@1t".getBytes("UTF-8");

        KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
        SecretKey tmp = factory.generateSecret(spec);
        byte[] encoded = tmp.getEncoded();
        return new SecretKeySpec(encoded, "AES");

    }

}*/
  
  

