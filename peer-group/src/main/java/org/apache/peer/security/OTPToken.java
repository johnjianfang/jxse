package org.apache.peer.security;

import java.io.Serializable;

/**
 * One time Password Token, which could be implemented
 * on smart card or other hardware devices. Here we use
 * a token to present it.
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public interface OTPToken extends Serializable {
	//method signature to get the id of the Token
	public String getSN();

	//method signature to generate one time password
	public String generateOTP();

	//method signature to generate a response
	//with a challenge
	public String handshakeResponse(String challenge);
}
