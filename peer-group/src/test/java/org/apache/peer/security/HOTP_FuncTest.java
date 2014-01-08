package org.apache.peer.security;

import org.apache.commons.codec.binary.Base64;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * A functional test to test the HOTP work flow
 *
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class HOTP_FuncTest {
    
	private static HOTPValidator validator;
	private static HOTPGenerator client;
	private static OTPToken token;
	private static OTPProfile profile;
	private static HOTPAlgorithm algorithm;
	private static String SECRET_STRING = "12345678901234567890";
	private static int lookAheadWindSize = 5;
	private static int throttlingParam = 5;
	private static boolean useLookAheadWind = true;
	private static boolean useLockout = true;
	private static int syncSequLen = 3;

	@BeforeClass
	public static void singleSetup(){
		algorithm = new HOTPAlgorithm();
		long initCount = 100;
		int codeDigits = 9;
		boolean addChecksum = true;
		int truncationOffset = 20;
		String SN = "321151351351";
		int stepSize = 100;
		int failedAttempts = 0;

		token = new OTPTokenImpl(SN, SECRET_STRING.getBytes(), initCount-5, codeDigits,
				addChecksum, truncationOffset, stepSize, algorithm);

		profile = new OTPProfile(SN, SECRET_STRING.getBytes(), initCount,
				System.currentTimeMillis(), codeDigits, addChecksum, truncationOffset,
				stepSize, failedAttempts);

		validator = new HOTPValidatorImpl(lookAheadWindSize, throttlingParam, algorithm,
				useLookAheadWind, useLockout);

		client = new HOTPGenerator(token, syncSequLen);
	}

	@Test
	public void testHandShake(){
		String challenge = validator.handshakeChallenge(profile);
		String decoded = new String(Base64.decodeBase64(challenge.getBytes()));

		System.out.println("Client and Server start handshake with Challenge " + challenge + " (" + decoded + ")");

		try {
			String otp = client.handshakeResponse(challenge);
			boolean succeeded = validator.validate(otp, profile);
			assertTrue(succeeded);
			System.out.println("Client and Server finished handshake with server counter "
					+ profile.getCounter() + " and Bias " + profile.getBias());
		} catch (HOTPException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testValidation(){
		testHandShake();
		int count = 10;

		String otp = null;
		for(int i=0; i<count; i++){
			try {
				otp = client.generateOTP();
				boolean succeeded = validator.validate(otp, profile);
				assertTrue(succeeded);
			} catch (HOTPException e) {
				fail(e.getLocalizedMessage());
			}
		}

		try {
			validator.validate(otp, profile);
			fail("Simulate replay attack, should failed here");
		} catch (HOTPException e) {
			System.out.println("Replay attack failed, exception " + e.getLocalizedMessage());
			assertTrue(true);
		}
	}

	@Test
	public void testLookAheadWnd(){
		testHandShake();
		try {
			client.generateOTP();
			client.generateOTP();
			String otp = client.generateOTP();
			boolean succeeded = validator.validate(otp, profile);
			assertTrue(succeeded);
		} catch (HOTPException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testSynchronize(){
		testHandShake();
		try {
			for(int i=0; i<=lookAheadWindSize-3; i++)
				client.generateOTP();

			String[] otps = client.generateSyncSequ();
			boolean succeeded = validator.sychronize(otps, profile);
			assertTrue(succeeded);
		} catch (HOTPException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testLookout(){
		testHandShake();
		try {
			for(int i=0; i<=lookAheadWindSize+3; i++)
				client.generateOTP();

			for(int i=0; i<=throttlingParam; i++){
				String otp = client.generateOTP();
				try {
					validator.validate(otp, profile);
				} catch (HOTPException e) {

				}
			}

			String otp = client.generateOTP();
			try {
				validator.validate(otp, profile);
				assertTrue(profile.getFailedAttempts() > throttlingParam);
				fail("Should throw exception here");
			} catch (HOTPException e) {
				System.out.println(e.getLocalizedMessage());
				assertTrue(true);
			}

		}catch (HOTPException e) {
			fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void testWorkflow(){
		//always first do HandShake to synchronize the counter and the bias
		profile.clearFailedAttempts();
		testHandShake();
		System.out.println("Client and Server handshake with counter " + profile.getCounter()
				+ " Bias " + profile.getBias());

		int count = 1000;
		String otp = null;
		Random rand = new Random();

		for(int i=0; i<count;i++){
			try {
				otp = client.generateOTP();
				System.out.println("Client generates OTP " + otp);
			} catch (HOTPException e) {
				otp = null;
				System.out.println("Client generates OTP exception " + e.getLocalizedMessage());
			}

			if(otp != null){
				if(rand.nextInt(100) > 20){
					try {
						boolean succeeded = validator.validate(otp, profile);
						if (succeeded) {
							System.out.println("Validation of OTP " + otp
									+ " succeeded. Server counter "
									+ profile.getCounter());
						} else {
							System.out.println("Validation of OTP " + otp
									+ " failed. Server counter "
									+ profile.getCounter());
						}
					} catch (HOTPException e) {
						System.out.println("Validation of OTP " + otp
								+ " failed. Server counter "
								+ profile.getCounter() + ". Exception " + e.getLocalizedMessage());

						String[] otps = null;
						System.out.println("Client and Server try to synchronize");
						try {
							otps = client.generateSyncSequ();
						} catch (HOTPException e1) {
							otps = null;
							System.out.println("Client generates Sync Sequence exception " + e1.getLocalizedMessage());
						}

						if(otps != null){
							try {
								boolean succeeded = validator.sychronize(otps, profile);
								if (succeeded) {
									System.out.println("Sync of OTP succeeded. Server counter "
													+ profile.getCounter());
								} else {
									System.out.println("Sync of OTP failed. Server counter "
													+ profile.getCounter());
								}
							} catch (HOTPException e2) {
								System.out.println("Sync of OTP failed. Server counter "
										+ profile.getCounter()
										+ ". Exception " + e2.getLocalizedMessage());
								System.out.println("Client and Server need to do handshake again");
								testHandShake();
							}
						}
					}
				}else{
					System.out.println("Client request for OTP " + otp + " is missed");
				}
			}
		}
	}

}
