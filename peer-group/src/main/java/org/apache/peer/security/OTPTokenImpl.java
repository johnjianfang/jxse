package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;

/**
 * An software implementation of the OTP Token
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class OTPTokenImpl implements OTPToken {

	private static final long serialVersionUID = 3305944945481471400L;
    
	private static String SEPARATOR = ":";

	//sequence number, i.e., the id of this token
	private String sn;

	//shared secret - MUST be at least 128 bits, RECOMMENDS 160 bits, i.e., 20 bytes
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

	//The HOTP algorithm generating OTP
	private HOTPAlgorithm algorithm;

	public OTPTokenImpl(String sn, byte[] secret, long counter, int otpLength, boolean addCheckSum,
			int truncationOffset, int stepSize, HOTPAlgorithm algorithm) {
		this.sn = sn;
		this.secret = secret;
		this.counter = counter;
		this.otpLength = otpLength;
		this.addCheckSum = addCheckSum;
		this.truncationOffset = truncationOffset;
		this.stepSize = stepSize;
		this.algorithm = algorithm;
		this.bias = System.currentTimeMillis();
	}

	public OTPTokenImpl(String sn, byte[] secret, long counter, long bias, int otpLength,
			boolean addCheckSum, int truncationOffset, int stepSize, HOTPAlgorithm algorithm) {
		this.sn = sn;
		this.secret = secret;
		this.counter = counter;
		this.bias = bias;
		this.otpLength = otpLength;
		this.addCheckSum = addCheckSum;
		this.truncationOffset = truncationOffset;
		this.algorithm = algorithm;
		this.stepSize = stepSize;
	}

	public String generateOTP() {
		if(algorithm != null){

			String otp = algorithm.generateOTP(this.secret, this.counter, this.bias,
					this.otpLength, this.addCheckSum, this.truncationOffset);

			//increase counter, wrap up when the maximum value is reached
			if(this.counter == Long.MAX_VALUE)
				this.counter = 1;
			else
				this.counter++;

			return otp;
		}

		return null;
	}

	public String getSN() {

		return this.sn;
	}

	public String handshakeResponse(String challenge) {
		if(challenge == null)
			return null;

		String decoded = new String(Base64.decodeBase64(challenge.getBytes()));

		String[] fields = decoded.split(SEPARATOR);
		if(fields == null || fields.length != 3)
			return null;

		long randomizer = Long.parseLong(fields[0]);
		long newcounter = Long.parseLong(fields[1]);
		long newbias = Long.parseLong(fields[2]);

		String otp = this.algorithm.generateOTP(this.secret, randomizer, newbias,
				this.otpLength, this.addCheckSum, this.truncationOffset);

		long inc = Long.parseLong(otp);
		int increment = (int) (inc%this.stepSize);
		this.bias = newbias;
		if(Long.MAX_VALUE - newcounter < increment )
			this.counter = increment - Long.MAX_VALUE - newcounter;
		else
			this.counter = newcounter + increment;

		otp = this.generateOTP();

		return otp;
	}
}
