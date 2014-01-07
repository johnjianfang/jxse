package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jul 3, 2009
 */
public class SecurityUtil {
    
    public static String encrypt(SecretKey sessionKey, String clearText){
        try {
            byte[] compressed = compress(clearText.getBytes());
//            byte[] encrypted = SymmetricCipher.desEncrypt(sessionKey, compressed);
            byte[] encrypted = SymmetricCipher.aesEncrypt(sessionKey, compressed);

            return new String(Base64.encodeBase64(encrypted), "UTF8");       
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage());
        } catch (NoSuchPaddingException e) {
            throw new SecurityException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new SecurityException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new SecurityException(e.getMessage());
        } catch (BadPaddingException e) {;
            throw new SecurityException(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(e.getMessage());
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    public static String decrypt(SecretKey sessionKey, String encryptedText) {
        try {
            byte[] decoded = Base64.decodeBase64(encryptedText.getBytes());
//            byte[] decrypted = SymmetricCipher.desDecrypt(sessionKey, decoded);
            byte[] decrypted = SymmetricCipher.aesDecrypt(sessionKey, decoded);
            byte[] decompressed = decompress(decrypted);

            return new String(decompressed);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e.getMessage());
        }catch (NoSuchPaddingException e) {
            throw new SecurityException(e.getMessage());
        } catch (InvalidKeyException e) {
            throw new SecurityException(e.getMessage());
        } catch (IllegalBlockSizeException e) {
            throw new SecurityException(e.getMessage());
        } catch (BadPaddingException e) {
            throw new SecurityException(e.getMessage());
        } catch (Exception e) {
            throw new SecurityException(e.getMessage());
        }
    }

    public static String base64Encode(String text){
        try {
            return new String(Base64.encodeBase64(text.getBytes()), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    public static String base64Decode(String text){
        return new String(Base64.decodeBase64(text.getBytes()));
    }

    public static String base64Encode(byte[] data){
        try {
            return new String(Base64.encodeBase64(data), "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new SecurityException(e.getMessage());
        }
    }

    public static String base64Decode(byte[] data){
        return new String(Base64.decodeBase64(data));
    }

    public static byte[] compress(byte[] data) {
        Deflater compressor = new Deflater();
        compressor.setLevel(Deflater.BEST_COMPRESSION);
        compressor.setInput(data);
        compressor.finish();

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        byte[] buf = new byte[1024];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (IOException e) {
            throw new SecurityException(e.getMessage());
        }

        return bos.toByteArray();
    }

    public static byte[] decompress(byte[] data) {
        Inflater decompressor = new Inflater();
        decompressor.setInput(data);

        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);

        byte[] buf = new byte[1024];
        while (!decompressor.finished()) {
            try {
                int count = decompressor.inflate(buf);
                bos.write(buf, 0, count);
            } catch (DataFormatException e) {
                throw new SecurityException(e.getMessage());
            }
        }
        try {
            bos.close();
        } catch (IOException e) {
            throw new SecurityException(e.getMessage());
        }

        return bos.toByteArray();
    }
}
