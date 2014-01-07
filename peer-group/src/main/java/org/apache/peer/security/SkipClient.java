package org.apache.peer.security;

import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */

public class SkipClient implements Client {

	private Skip protocol;
	private SecretKey key;

	public SkipClient() {
		protocol = new Skip();
		key = null;
	}

	public byte[] generatePublicKey(){
		byte[] clientPubKeyEnc;

		try {
			clientPubKeyEnc = protocol.generatePublicKey();
		} catch (InvalidKeyException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (InvalidAlgorithmParameterException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		}

		return clientPubKeyEnc;
	}

	public SecretKey generateSecretKey(byte[] serverPubKeyEnc) {

        return this.generateAESKey(serverPubKeyEnc);
	}

	protected SecretKey generateDESKey(byte[] serverPubKeyEnc) {

		try {
			key = protocol.generateDESKey(serverPubKeyEnc);
		} catch (InvalidKeyException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (NoSuchAlgorithmException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (InvalidKeySpecException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (IllegalStateException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		}

		return key;
	}

	protected SecretKey generateAESKey(byte[] serverPubKeyEnc) {

		try {
			key = protocol.generateAESKey(serverPubKeyEnc);
		} catch (InvalidKeyException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (NoSuchAlgorithmException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (InvalidKeySpecException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (IllegalStateException e) {
			throw new  SecurityException(e.getLocalizedMessage(), e.getCause());
		}

		return key;
	}

	public SecretKey getSecreteKey() {

		return key;
	}
}
