package org.apache.peer.security;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 *
 * Asymmetric key cryption, i.e., public and private key pairs
 * 
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class AsymmetricCiper {
        public static final String RSA_NONE_PKCS1PADDING_XFORM = "RSA/NONE/PKCS1PADDING";

        public static byte[] encrypt(byte[] data, PublicKey key,
            String xform) throws Exception
        {
          Cipher cipher = Cipher.getInstance(xform);
          cipher.init(Cipher.ENCRYPT_MODE, key);

          return cipher.doFinal(data);
        }

        public static byte[] decrypt(byte[] data, PrivateKey key,
            String xform) throws Exception
        {
          Cipher cipher = Cipher.getInstance(xform);
          cipher.init(Cipher.DECRYPT_MODE, key);

          return cipher.doFinal(data);
        }

        public static KeyPair createRSAKeyPair() throws Exception
        {
            // Generate a key-pair
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            // initialize keysize
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            return keyPair;
        }

        public static PublicKey getPublicKey(KeyPair pair)
        {

            return pair.getPublic();
        }

        public static byte[] getPublicKeyAsBytes(KeyPair pair)
        {

            return pair.getPublic().getEncoded();
        }

        public static PrivateKey getPrivateKey(KeyPair pair)
        {

            return pair.getPrivate();
        }

        public static byte[] getPrivateKeyAsBytes(KeyPair pair)
        {

            return pair.getPrivate().getEncoded();
        }
}
