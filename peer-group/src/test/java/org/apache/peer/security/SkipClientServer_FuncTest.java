package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.peer.entity.KeyExchangeResponse;
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
public class SkipClientServer_FuncTest {
	private static Client client;
	private static Server server;

	@BeforeClass
	public static void singleSetup(){
		client = new SkipClient();
		server = new SkipServer();
	}

	@AfterClass
	public static void singleTearDown() {
		server = null;
		client = null;
	}

	@Test
	public void testAgree(){

		try{
			byte[] clientPubKeyEnc = client.generatePublicKey();
            KeyExchangeResponse response = server.agree(clientPubKeyEnc);
			byte[] serverPubKeyEnc = response.getPubKeyEnc();
            SecretKey key = response.getKey();
			client.generateSecretKey(serverPubKeyEnc);

			System.out.println("Generated DES Key for the Server is (Base 64): "
			        + new String(Base64.encodeBase64(key.getEncoded()), "UTF8"));

			System.out.println("Generated DES Key for the Client is (Base 64): "
			        + new String(Base64.encodeBase64(client.getSecreteKey().getEncoded()), "UTF8"));

		    byte[] cleartext = "This is just an example".getBytes();
			byte[] clientEncrypted = SymmetricCipher.aesEncrypt(client.getSecreteKey(), cleartext);

			System.out.println("DES encryption for message \"" + new String(cleartext) + "\" is (Base 64): "
			        + new String(Base64.encodeBase64(clientEncrypted), "UTF8"));

			byte[] recovered = 	SymmetricCipher.aesDecrypt(key, clientEncrypted);
	        System.out.println("DES Decryption for message (Base 64)\""
	                + new String(Base64.encodeBase64(clientEncrypted), "UTF8")
	                + "\" is: " + new String(recovered));

			assertTrue(java.util.Arrays.equals(cleartext, recovered));

		}catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void loadTest(){
		int count = 100;

		long start = System.currentTimeMillis();
		for(int i=0;i<count;i++){
			System.out.println("Load Test: counter = " + (i+1));

			testAgree();
		}
		long end = System.currentTimeMillis();
		int total = (int) (end - start);
		int average = total/count;
		System.out.println("Load test finished: Total time " + total + " ms, average time " + average + " ms");
	}
}
