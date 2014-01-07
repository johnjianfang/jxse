package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

/**
 * The implementation of the HOTP validator, i.e., the server
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class HOTPValidatorImpl implements HOTPValidator {
	private static String INVALID_OTP_ALGORITHM = "Invalid OTP algorithm or OTP algorithm is not set";
	private static String FAILED_ATEMPTS_EXCEED = "Access denied because the number of failed attempts exceed the limit";
	private static String OTP_VALIDATION_FAILED = "Failed to validate the OTP";
	private static int TYPICAL_SIZE = 64;
	private static String SEPARATOR = ":";
	private static SecureRandom rand = new SecureRandom();

	//If the OTP received by the server does not match the value calculated
	//by the client, the server initiate the resynch protocol (look-ahead window)
	//before it requests another pass
	private int lookAheadWndSize;

	//the window size for the validator to look at during synchronization
	private int syncWndSize;

	//The maximum number of authorized attempts before the server
	//lock out the user account
	private int throttlingParam;

	//The HOTP algorithm generating OTP
	private HOTPAlgorithm algorithm;

	private boolean useLookAheadWnd;

	private boolean useLockout;


	/**
	 * @param lookAheadWindSize   look-ahead window size
	 * @param throttlingParam    the maximum number of authorized attempts before the server
	 * 							 lock out the user account
	 * @param algorithm			 the HOTP algorithm generating OTP
	 * @param useLookAheadWind	 whether we use the look ahead window
	 * @param useLockout	     whether we use the lockout scheme to prevent brute force attacks
	 */
	public HOTPValidatorImpl(int lookAheadWindSize, int throttlingParam, HOTPAlgorithm algorithm,
			boolean useLookAheadWind, boolean useLockout) {
		this.lookAheadWndSize = lookAheadWindSize;
		this.throttlingParam = throttlingParam;
		this.algorithm = algorithm;
		this.useLookAheadWnd = useLookAheadWind;
		this.syncWndSize = this.lookAheadWndSize;
		this.useLockout = useLockout;
	}

	public HOTPValidatorImpl(int lookAheadWindSize, int syncWndSize, int throttlingParam,
			HOTPAlgorithm algorithm, boolean useLookAheadWind, boolean useLockout) {
		this.lookAheadWndSize = lookAheadWindSize;
		this.throttlingParam = throttlingParam;
		this.algorithm = algorithm;
		this.useLookAheadWnd = useLookAheadWind;
		this.syncWndSize = syncWndSize;
		this.useLockout = useLockout;
	}

	private boolean validateOTP(String otp, OTPProfile profile){

		String expected = this.algorithm.generateOTP(profile.getSecret(), profile.getCounter(),
				profile.getBias(), profile.getOtpLength(),
				profile.isAddCheckSum(), profile.getTruncationOffset());

		if(expected.equals(otp)){
			profile.clearFailedAttempts();
			profile.increaseCounter();

			return true;
		}

		profile.increaseFailAttempts();

		return false;
	}

	private boolean validateOTPWithWnd(String otp, OTPProfile profile, int wndSize){

		long counter = profile.getCounter();

		for(int i=0; i<wndSize; i++){
			String expected = this.algorithm.generateOTP(profile.getSecret(), counter++,
					profile.getBias(), profile.getOtpLength(), profile.isAddCheckSum(),
					profile.getTruncationOffset());


			if(expected.equals(otp)){
				profile.clearFailedAttempts();
				profile.increaseCounter(i+1);

				return true;
			}
		}

		profile.increaseFailAttempts();

		return false;
	}

	public boolean sychronize(String[] otps, OTPProfile profile) {
		if(otps != null){
			//only need to validate the highest OTP
			String otp = otps[otps.length-1];
			//modified here, user syncWndSize to extend the number of counters to look at
			int num = otps.length + this.syncWndSize;
			if(this.useLookAheadWnd)
				num += this.lookAheadWndSize;

			boolean isSuccessful = this.validateOTPWithWnd(otp, profile, num);

			if(isSuccessful){
				return true;
			}

			//if not successful, return validation failed or ask the client to synchronize the counter again
			profile.increaseFailAttempts();

			throw new HOTPException(HOTPCode.VALIDATION_FAILED, OTP_VALIDATION_FAILED);
		}else{

			throw new HOTPException(HOTPCode.VALIDATION_FAILED, OTP_VALIDATION_FAILED);
		}
	}

	public boolean validate(String otp, OTPProfile profile) throws HOTPException {

		//check if we need to block the access in the first place
		if(this.useLockout && profile.getFailedAttempts() > this.throttlingParam)
			throw new HOTPException(HOTPCode.ACCESS_DENIED, FAILED_ATEMPTS_EXCEED);

		if(this.algorithm == null)
			throw new HOTPException(HOTPCode.INVALID_ALGORITHM, INVALID_OTP_ALGORITHM);

		//first validate the OTP directly
		boolean isSuccessful = validateOTP(otp, profile);

		if(isSuccessful)
			return true;

		//if not succeeded, try the resynch protocol, i.e., use the look-ahead window to check the OTP
		if(this.useLookAheadWnd){
			isSuccessful = this.validateOTPWithWnd(otp, profile, this.lookAheadWndSize);

			if(isSuccessful)
				return true;
		}

		//if not successful, return validation failed or ask the client to synchronize the counter
		profile.increaseFailAttempts();

		throw new HOTPException(HOTPCode.VALIDATION_FAILED, OTP_VALIDATION_FAILED);
	}

	public String handshakeChallenge(OTPProfile profile) throws HOTPException {
		StringBuffer sb = new StringBuffer(TYPICAL_SIZE);
		long randomizer = rand.nextLong();
		if(randomizer < 0)
			randomizer = -randomizer;

		profile.newBias();
		long bias = profile.getBias();
		long counter = profile.getCounter();

		sb.append(randomizer).append(SEPARATOR).append(counter).append(SEPARATOR).append(bias);

		String otp = algorithm.generateOTP(profile.getSecret(), randomizer,
				profile.getBias(), profile.getOtpLength(),
				profile.isAddCheckSum(), profile.getTruncationOffset());

		long inc = Long.parseLong(otp);
		int increment = (int) (inc%profile.getStepSize());
		profile.increaseCounter(increment);

		String result = null;
		try {
			result = new String(Base64.encodeBase64(sb.toString().getBytes()), "UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new HOTPException(HOTPCode.INVALID_ALGORITHM, e.getLocalizedMessage());
		}

		return result;
	}

}
