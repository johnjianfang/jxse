package org.apache.peer.security;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class OTPTokenImpl_UT {
	private static String SECRET_STRING = "12345678901234567890";
	private static HOTPAlgorithm algorithm;

	@BeforeClass
	public static void singleSetup() {
		algorithm = new HOTPAlgorithm();
	}

	@AfterClass
	public static void singleTearDown() {
		algorithm = null;
	}

	@Test
	public void testGenerateOTP(){
		long initCount = 100;
		int codeDigits = 9;
		boolean addChecksum = true;
		int truncationOffset = 20;
		String SN = "321151351351";
		OTPToken token = new OTPTokenImpl(SN, SECRET_STRING.getBytes(), initCount, codeDigits,
				addChecksum, truncationOffset, 100, algorithm);

		for (int i = 0; i < 10; i++) {
			String otp;
			try {
				otp = token.generateOTP();
				System.out.println("Count " + (initCount + i) + " OTP " + otp);
			} catch (HOTPException e) {
				fail(e.getLocalizedMessage());
			}
		}
	}

    @Test
    public void testSessionRequestId(){
        OTPToken token = this.generateOTPToken("SACCT-1246904840952-kOIxdgfuGCj9-1", null);
        long initCount = 1000;

		for (int i = 0; i < 10; i++) {
			String otp;
			try {
				otp = token.generateOTP();
				System.out.println("Count " + (initCount + i) + " OTP " + otp);
			} catch (HOTPException e) {
				fail(e.getLocalizedMessage());
			}
        }
    }

    protected OTPToken generateOTPToken(String sn, byte[] sharedSecret){

//        return new OTPTokenImpl(sn, sharedSecret, 1000, 9, true, 20, 100, new HOTPAlgorithm());
//        return new OTPTokenImpl(sn, sn.getBytes(), 1000, 0, 9, true, 20, 100, new HOTPAlgorithm());
          return new OTPTokenImpl(sn, "12131315315".getBytes(), 1000, 0, 9, true, 20, 100, new HOTPAlgorithm());
    }
}
