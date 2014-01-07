package org.apache.peer.security;


import org.apache.peer.entity.KeyExchangeResponse;

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
public class SkipServer implements Server {

	public KeyExchangeResponse agree(byte[] clientPubKeyEnc){
		KeyExchangeResponse response = new KeyExchangeResponse();

		try {
            Skip protocol = new Skip();
            byte[] serverPubKeyEnc = protocol.generatePublicKey();
//			SecretKey key = protocol.generateDESKey(clientPubKeyEnc);
            SecretKey key = protocol.generateAESKey(clientPubKeyEnc);
            response.setKey(key);
            response.setPubKeyEnc(serverPubKeyEnc);
		} catch (InvalidKeyException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (InvalidAlgorithmParameterException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (InvalidKeySpecException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		} catch (IllegalStateException e) {
			throw new SecurityException(e.getLocalizedMessage(), e.getCause());
		}
		
		return response;
	}

}
