package org.apache.peer.security;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Implement the Simple Key Management for Internet Protocol (SKIP)
 * Here consider only 2-party key exchanges
 *
 * generatePublicKey() must be called first before call
 *
 * 		generateDESKey()
 *
 * or
 *
 * 		generateSharedSecret()
 *
 * The workflow is as follows,
 *
 *
 *        PEER A                                   PEER B
 *
 * 1)   byte[] aPub =generateDESKey();       ------------->
 *
 * 2) 	   <---------------------		   byte[] bPub =generateDESKey();
 *
 * 3)   Key = generateDESKey(bPub)          key = generateDESKey(aPub)
 *
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 *
 * Date: Jun 26, 2009
 * 
 */
public class Skip {

	private KeyAgreement keyAgree;

	public byte[] generatePublicKey() throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException
	{
		DHParameterSpec dhSkipParamSpec= DHParam.getSkipDHParamSpec();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DH");
        keyPairGen.initialize(dhSkipParamSpec);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        keyAgree = KeyAgreement.getInstance("DH");
        keyAgree.init(keyPair.getPrivate());

        // encode the public key, and sends it over to the peer.
        byte[] pubKeyEnc = keyPair.getPublic().getEncoded();

        return pubKeyEnc;
	}

	public SecretKey generateDESKey(byte[] peerPubKeyEnc) throws NoSuchAlgorithmException,
		InvalidKeySpecException, InvalidKeyException, IllegalStateException
	{
	 	KeyFactory keyFacactory = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(peerPubKeyEnc);
		PublicKey peerPubKey = keyFacactory.generatePublic(x509KeySpec);
		keyAgree.doPhase(peerPubKey, true);
	    SecretKey desKey = keyAgree.generateSecret("DES");

		return desKey;
	}

	public SecretKey generateAESKey(byte[] peerPubKeyEnc) throws NoSuchAlgorithmException,
		InvalidKeySpecException, InvalidKeyException, IllegalStateException
	{
	 	KeyFactory keyFacactory = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(peerPubKeyEnc);
		PublicKey peerPubKey = keyFacactory.generatePublic(x509KeySpec);
		keyAgree.doPhase(peerPubKey, true);
	    SecretKey desKey = keyAgree.generateSecret("AES");

		return desKey;
	}

	public byte[] generateSharedSecret(byte[] peerPubKeyEnc) throws NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidKeyException, IllegalStateException
	{
	 	KeyFactory keyFacactory = KeyFactory.getInstance("DH");
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(peerPubKeyEnc);
		PublicKey peerPubKey = keyFacactory.generatePublic(x509KeySpec);
		keyAgree.doPhase(peerPubKey, true);

		byte[] sharedSecret = keyAgree.generateSecret();

		return sharedSecret;
	}
}
