package org.apache.peer.security;

import javax.crypto.SecretKey;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 * 
 */
public interface Client {
    
	public byte[] generatePublicKey();

	public SecretKey generateSecretKey(byte[] serverPubKeyEnc);

	public SecretKey getSecreteKey();
}
