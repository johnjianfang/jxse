package org.apache.peer.security;


import org.apache.peer.entity.KeyExchangeResponse;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public interface Server {
    
	public KeyExchangeResponse agree(byte[] clientPubKeyEnc);

}
