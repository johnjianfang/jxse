package org.apache.peer.security;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class SymmetricCipher {
    //initialization vector
    private static byte[] iv =
    { 0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b, 0x0c, 0x0d };
    private final static String DES_ECB_PKCS5PADDING_XFORM = "DES/ECB/PKCS5Padding";
    private final static String DES_ECB_NOPADDING_XFORM = "DES/ECB/NoPadding";

    private final static String AES_ECB_PKC5PADDING = "AES/ECB/PKCS5Padding";

    public static byte[] aesEncrypt(SecretKey aesKey, byte[] data) throws Exception {
        Cipher aesCipher = Cipher.getInstance(AES_ECB_PKC5PADDING);

        // Initialize the cipher for encryption
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);

        return aesCipher.doFinal(data);
    }

    public static byte[] aesDecrypt(SecretKey aesKey, byte[] data) throws Exception {
        Cipher aesCipher = Cipher.getInstance(AES_ECB_PKC5PADDING);

        // Initialize the cipher for Decryption
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
       
        return aesCipher.doFinal(data);
    }

    public static byte[] aesEncrypt(String key, byte[] data) throws Exception {
        MessageDigest digester = MessageDigest.getInstance("MD5");
        char[] password = key.toCharArray();
        for (int i = 0; i < password.length; i++) {
            digester.update((byte) password[i]);
        }
        byte[] passwordData = digester.digest();

        Key secretkey = new SecretKeySpec(passwordData, "AES");

        return aesEncrypt((SecretKey) secretkey, data);
    }

    public static byte[] aesDecrypt(String key, byte[] data) throws Exception {
        MessageDigest digester = MessageDigest.getInstance("MD5");
        char[] password = key.toCharArray();
        for (int i = 0; i < password.length; i++) {
            digester.update((byte) password[i]);
        }
        byte[] passwordData = digester.digest();

        Key secretkey = new SecretKeySpec(passwordData, "AES");
        
        return aesDecrypt((SecretKey)secretkey, data);
    }


    public static byte[] encrypt(byte[] data,
            SecretKey key, String xform) throws Exception
    {
        Cipher cipher;
        cipher = Cipher.getInstance(xform);
        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ips);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data,
            SecretKey key, String xform) throws Exception
    {
        Cipher cipher = Cipher.getInstance(xform);
        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ips);

        return cipher.doFinal(data);
    }

    public static SecretKey createDESKey() throws NoSuchAlgorithmException
    {
        // Generate a secret key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        // DES keysize is fixed: 56
        keyGenerator.init(56);
        SecretKey key = keyGenerator.generateKey();

        return key;
    }

    public static byte[] getDESKeyAsBytes(SecretKey key)
    {

        return key.getEncoded();
    }

    public static byte[] desEncrypt(byte[] data,
            String key, String xform) throws Exception
    {
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher desCipher = Cipher.getInstance(xform);
        desCipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return desCipher.doFinal(data);
    }

    public static byte[] desDecrypt(byte[] data,
            String key, String xform) throws Exception
    {
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        Cipher desCipher = Cipher.getInstance(xform);
        desCipher.init(Cipher.DECRYPT_MODE, secretKey);

        return desCipher.doFinal(data);
    }

    public static byte[] desEncrypt(final String innerKey, final String msg)
    	throws UnsupportedEncodingException, Exception
    {
    	byte[] encrypted = desEncrypt(msg.getBytes("UTF8"), innerKey, DES_ECB_PKCS5PADDING_XFORM);

        return encrypted;
    }

    public static byte[] desEncrypt(final String innerKey, final byte[] msg)
    	throws UnsupportedEncodingException, Exception
    {
    	byte[] encrypted = desEncrypt(msg, innerKey, DES_ECB_PKCS5PADDING_XFORM);

    	return encrypted;
    }

    public static byte[] desDecrypt(final String innerKey, final byte[] encrypted) throws Exception{

    	byte[] decrypted = desDecrypt(encrypted, innerKey, DES_ECB_PKCS5PADDING_XFORM);

		return decrypted;
    }

    public static byte[] desEncrypt(SecretKey key, final byte[] msg) throws NoSuchAlgorithmException,
    	NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance(DES_ECB_PKCS5PADDING_XFORM);
        desCipher.init(Cipher.ENCRYPT_MODE, key);

        return desCipher.doFinal(msg);
    }

    public static byte[] desDecrypt(SecretKey key, final byte[] encrypted) throws NoSuchAlgorithmException,
    	NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance(DES_ECB_PKCS5PADDING_XFORM);
        desCipher.init(Cipher.DECRYPT_MODE, key);

        return desCipher.doFinal(encrypted);
    }
}
