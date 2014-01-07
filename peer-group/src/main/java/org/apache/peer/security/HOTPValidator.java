package org.apache.peer.security;


/**
 * The HOTP validator, i.e., the server
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public interface HOTPValidator {

	//validate if the one time password (OTP) is correct
	boolean validate(String otp, OTPProfile profile);

	//sychronize the counter with the HOTP generator, i.e., the client
	boolean sychronize(String[] otps, OTPProfile profile);

	//generate the challenge for handshake
	String handshakeChallenge(OTPProfile profile);
}
