package org.apache.peer.security;

import java.io.Serializable;

/**
 * Similar to OTP Token, but it is a record of the OTP token
 * maintained by the server thus, the setters and getters
 * must be used for the server to change the states of the
 * OTP profile
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class OTPProfile implements Serializable {

	private static final long serialVersionUID = 3305944945481471400L;
    
	//sequence number, i.e., the id  of the profile
	private String sn;

	//shared secret - MUST be at least 128 bits, RECOMMENDS 160 bites, i.e., 20 bytes
	private byte[] secret;

	//counter, a moving factor in
	//2 factor authentication
	private long counter;

	//additional input data
	private long bias;

	//the length of the generated OTP
	private int otpLength;

	//whether we add a check sum to the generate OTP
	private boolean addCheckSum;

	//the offset into the MAC result to
	//begin truncation.  If this value is out of
	//the range of 0 ... 15, then dynamic
	//truncation  will be used.
	//Dynamic truncation is when the last 4
	//bits of the last byte of the MAC are
	//used to determine the start offset.
	private int truncationOffset;

	//the maximum counter increment during handshake
	private int stepSize;

	//number of failed authentication attempts
	private int failedAttempts;

	//increase the number of failed attempts
	public int increaseFailAttempts(){

		return ++this.failedAttempts;
	}

	//return the number of failed attempts
	public int getFailedAttempts(){

		return this.failedAttempts;
	}

	//reset the number of failed attempts
	public void clearFailedAttempts(){
		this.failedAttempts = 0;
	}

	public OTPProfile(String sn, byte[] secret, long counter, long bias,
                      int otpLength, boolean addCheckSum, int truncationOffset, int stepSize, int failedAttempts) {
		this.sn = sn;
		this.secret = secret;
		this.counter = counter;
		this.bias = bias;
		this.otpLength = otpLength;
		this.addCheckSum = addCheckSum;
		this.truncationOffset = truncationOffset;
		this.stepSize = stepSize;
		this.failedAttempts = failedAttempts;
	}

	public final boolean isAddCheckSum() {
		return this.addCheckSum;
	}

	public final long getCounter() {
		return this.counter;
	}

	public final int getOtpLength() {
		return this.otpLength;
	}

	public final byte[] getSecret() {
		return this.secret;
	}

	public final int getTruncationOffset() {
		return this.truncationOffset;
	}

	public void increaseCounter() {
		this.counter++;
	}

	public String getSN() {
		return this.sn;
	}

	/**
	 * @return the bias
	 */
	public final long getBias() {
		return this.bias;
	}

	public final void newBias() {
		this.bias = System.currentTimeMillis();
	}

	/**
	 * @return the stepSize
	 */
	public final int getStepSize() {
		return stepSize;
	}

	public void increaseCounter(int inc) {
		if(Long.MAX_VALUE - this.counter < inc )
			this.counter = inc - Long.MAX_VALUE - this.counter;
		else
			this.counter += inc;
	}
}
