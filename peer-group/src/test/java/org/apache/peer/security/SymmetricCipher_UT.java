package org.apache.peer.security;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 *         Date: Jul 7, 2009
 *
 */
public class SymmetricCipher_UT {

    @Test
    public void testAES(){
        String hello = "Hello, this is a test message";
        String passPhrase = "P@ssw0rd";
        try {
            byte[] encrypted = SymmetricCipher.aesEncrypt(passPhrase, hello.getBytes());
            byte[] decrypted = SymmetricCipher.aesDecrypt(passPhrase, encrypted);
            assertEquals(hello, new String(decrypted));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
