package org.apache.peer.security;


/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 *
 */
public class HOTPGenerator {
    
	private OTPToken token;
	//the resynchronization sequence length. The system requires
	// the generator to send a sequence of (say, 2, 3) HOTP values
	//for resynchronization purpose, since forging a sequence of
	//consecutive HOTP value is even more difficult than guessing
	//a single HOTP value
	private int syncSequLen;
	private static int DEFAULT_SYNC_SEQUENC_LENGTH = 3;

	/**
	 * @param token the OTP Token
	 * @param syncSequLen the resynchronization sequence length
	 */
	public HOTPGenerator(OTPToken token, int syncSequLen) {
		this.token = token;
		this.syncSequLen = syncSequLen;

		//safe guard syncSequLen using a default value
		if(this.syncSequLen <1)
			this.syncSequLen = DEFAULT_SYNC_SEQUENC_LENGTH;
	}

	public String generateOTP() throws HOTPException {
		if(this.token != null){

			return this.token.generateOTP();
		}

		return null;
	}

	public String[] generateSyncSequ() throws HOTPException {
		String[] otps = null;

		if(this.token != null){
			otps = new String[this.syncSequLen];
			for(int i=0; i<this.syncSequLen; i++){
				otps[i] = this.generateOTP();
			}
		}

		return otps;
	}

	public String handshakeResponse(String challenge) throws HOTPException {
		if(this.token != null){

			return this.token.handshakeResponse(challenge);
		}

		return null;
	}    
}
