package org.apache.peer.security;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Implement the HOTP algorithm based on the reference implementation
 * in RFC 4226
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class HOTPAlgorithm  implements Serializable {
    
	private static final long serialVersionUID = 7522068687254843L;

	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final String HAMC_SHA_1 = "HMAC-SHA-1";
	private static final String RAW = "RAW";
    private static final String EMPTY = "";
    private static final char ZERO= '0';

	private static final int[] doubleDigits = { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 };
	private static final int[] DIGITS_POWER
	//  0   1    2    3     4       5       6        7          8           9
	= { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000,  1000000000};

	private int calcChecksum(long num, int digits) {
		boolean doubleDigit = true;
		int total = 0;
		while (0 < digits--) {
			int digit = (int) (num % 10);
			num /= 10;
			if (doubleDigit) {
				digit = doubleDigits[digit];
			}
			total += digit;
			doubleDigit = !doubleDigit;
		}

		int result = total % 10;
		if (result > 0) {
			result = 10 - result;
		}

		return result;
	}

	private byte[] hmac_sha1(byte[] keyBytes, byte[] text)
		throws HOTPException {

		Mac hmacSha1;
		try {
			hmacSha1 = Mac.getInstance(HMAC_SHA1);
		} catch (NoSuchAlgorithmException e) {
			try {

				hmacSha1 = Mac.getInstance(HAMC_SHA_1);
			} catch (NoSuchAlgorithmException e1) {

				throw new HOTPException(HOTPCode.INVALID_ALGORITHM, e1.getLocalizedMessage());
			}
		}

		SecretKeySpec macKey = new SecretKeySpec(keyBytes, RAW);
		try {

			hmacSha1.init(macKey);
		} catch (InvalidKeyException e) {

			throw new HOTPException(HOTPCode.INVALID_ALGORITHM, e.getLocalizedMessage());
		}

		return hmacSha1.doFinal(text);
	}

	public String generateOTP(byte[] secret, long movingFactor,
			long bias, int codeDigits, boolean addChecksum, int truncationOffset)
			throws HOTPException {

		String result = null;

		//The truncked 4 bytes including 31 bits, which are at most 10 digits
		//the highest digits are 0-2, not random enough. The best range will be
		// 6 ~ 9 digits.
		if(codeDigits < 6)
			codeDigits = 6;
		if(codeDigits > 9)
			codeDigits = 9;
		int digits = addChecksum ? (codeDigits + 1) : codeDigits;

		// put movingFactor value into text byte array
		// the counter is long, maximum length is 64 bits, i.e., 8 bytes
		// the bias is also long, 8 bytes
		byte[] text = new byte[16];

		for (int i = text.length - 1; i >= 8; i--) {
			text[i] = (byte) (movingFactor & 0xff);
			movingFactor >>= 8;
		}

		for (int i = 7; i >= 0; i--) {
			text[i] = (byte) (bias & 0xff);
			bias >>= 8;
		}

		// compute hmac hash
		byte[] hash = hmac_sha1(secret, text);

		// put selected bytes into result int
		int offset = hash[hash.length - 1] & 0xf;
		if ((0 <= truncationOffset) && (truncationOffset < (hash.length - 4))) {
			offset = truncationOffset;
		}
		//31 bits, remove the first bit to avoid unsigned vs. signed ambiguity
		long binary = ((hash[offset] & 0x7f) << 24)
				| ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8)

				| (hash[offset + 3] & 0xff);

		long otp = binary % DIGITS_POWER[codeDigits];

		if (addChecksum) {
			otp = (otp * 10) + calcChecksum(otp, codeDigits);
		}

		result = rjzf(Long.toString(otp), digits);

		return result;
	}

    private String rj(String field, int width, char fill) {
        if (field == null) {
            field = EMPTY;
        }

        if (field.length() > width) {
            System.err.println("Field '" + field + "' has width greater than "
                    + width);
            return field.substring(0, width);
        }

        StringBuilder sb = new StringBuilder(width);
        sb.append(field);

        for (int j = width - field.length(); j != 0; j--)
            sb.insert(0, fill);

        return sb.toString();
    }

    public final String rjzf(String field, int width) {
        return rj(field, width, ZERO);
    }
}
