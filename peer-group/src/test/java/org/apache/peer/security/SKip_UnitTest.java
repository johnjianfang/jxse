package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class SKip_UnitTest {
	private static Skip server;
	private static Skip client;

	@BeforeClass
	public static void singleSetup(){
		server = new Skip();
		client = new Skip();
	}

	@AfterClass
	public static void singleTearDown() {
		client = null;
		server = null;
	}

	@Test
	public void testGenerateDESKey(){
		try {
			byte[] clientPub = client.generatePublicKey();
			byte[] serverPub = server.generatePublicKey();
			SecretKey serverKey = server.generateDESKey(clientPub);
			SecretKey clientKey = client.generateDESKey(serverPub);

            System.out.println("Generated DES Key for the Server is (Base 64): "
                    + new String(Base64.encodeBase64(serverKey.getEncoded()), "UTF8"));

            System.out.println("Generated DES Key for the Client is (Base 64): "
                    + new String(Base64.encodeBase64(clientKey.getEncoded()), "UTF8"));

		    byte[] cleartext = "This is just an example".getBytes();
			byte[] clientEncrypted = SymmetricCipher.desEncrypt(clientKey, cleartext);
            System.out.println("DES encryption for message \"" + new String(cleartext) + "\" is (Base 64): "
                    + new String(Base64.encodeBase64(clientEncrypted), "UTF8"));

			byte[] recovered = 	SymmetricCipher.desDecrypt(serverKey, clientEncrypted);
            System.out.println("DES Decryption for message (Base 64)\""
                    + new String(Base64.encodeBase64(clientEncrypted), "UTF8")
                    + "\" is: " + new String(recovered));

			assertTrue(java.util.Arrays.equals(cleartext, recovered));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGenerateAESKey(){
		try {
			byte[] clientPub = client.generatePublicKey();
			byte[] serverPub = server.generatePublicKey();
			SecretKey serverKey = server.generateAESKey(clientPub);
			SecretKey clientKey = client.generateAESKey(serverPub);

            System.out.println("AES key length " + serverKey.getEncoded().length);
            
            System.out.println("Generated AES Key for the Server is (Base 64): "
                    + new String(Base64.encodeBase64(serverKey.getEncoded()), "UTF8"));

            System.out.println("Generated AES Key for the Client is (Base 64): "
                    + new String(Base64.encodeBase64(clientKey.getEncoded()), "UTF8"));

		    byte[] cleartext = "This is just an example".getBytes();
			byte[] clientEncrypted = SymmetricCipher.aesEncrypt(clientKey, cleartext);
            System.out.println("AES encryption for message \"" + new String(cleartext) + "\" is (Base 64): "
                    + new String(Base64.encodeBase64(clientEncrypted), "UTF8"));

			byte[] recovered = 	SymmetricCipher.aesDecrypt(serverKey, clientEncrypted);
            System.out.println("AES Decryption for message (Base 64)\""
                    + new String(Base64.encodeBase64(clientEncrypted), "UTF8")
                    + "\" is: " + new String(recovered));

			assertTrue(java.util.Arrays.equals(cleartext, recovered));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGenerateSharedSecret(){
		try {
			byte[] clientPub = client.generatePublicKey();
			byte[] serverPub = server.generatePublicKey();
			byte[] serverSharedSecret = server.generateSharedSecret(clientPub);
			byte[] clientSharedSecret = client.generateSharedSecret(serverPub);

            System.out.println("Generated Shared Secret for the Server is (Base 64): "
                    + new String(Base64.encodeBase64(serverSharedSecret), "UTF8"));

            System.out.println("Generated Shared Secret for the Client is (Base 64): "
                    + new String(Base64.encodeBase64(clientSharedSecret), "UTF8"));

			assertTrue(java.util.Arrays.equals(serverSharedSecret, clientSharedSecret));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
