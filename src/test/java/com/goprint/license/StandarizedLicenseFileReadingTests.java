package com.goprint.license;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.Test;

public class StandarizedLicenseFileReadingTests {

	private static Logger log = Logger.getLogger(StandarizedLicenseFileReadingTests.class);

	@Test
	public void readOKInputMinimumFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("OkInputMinimum.txt", true));
	}

	@Test
	public void readOKInputMinimumSignedFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("OkInputMinimumSigned.txt", true));
	}

	@Test
	public void readOKInputFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("OkInput.txt", true));
	}

	@Test
	public void readOKEmptyFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("OkInputSigned.txt", true));
	}

	@Test
	public void readOKSignedFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("OkInputSigned.txt", true));
	}

	@Test
	public void readKOInputFileWrongParameter() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("KOInputFileWrongParameter.txt", false));
	}

	@Test
	public void readKOInputFileNoBeginSignature() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("KOInputFileNoBeginSignature.txt", false));
	}

	@Test
	public void readKOInputFileNoEndSignature() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("KOInputFileNoEndSignature.txt", false));
	}

	@Test
	public void readKOInputFileTwoSignatures() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("KOInputFileTwoSignatures.txt", false));
	}

	@Test
	public void readKOFileDuplicatedField() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(check("KOFileDuplicatedField.txt", false));
	}

	/**
	 * Checks if the read of the file has been successful
	 * 
	 * @param fileName
	 *            path to the file to read during test
	 * @param shouldReadGoRight
	 *            true iff the read process should go right
	 * @return true iff the read process has gone as expected
	 */
	private boolean check(String fileName, boolean shouldReadGoRight) {
		StandarizedLicenseFile file;
		boolean result = false;
		try {
			file = new StandarizedLicenseFile(Constants.BASE_INPUTS + fileName);
			result = shouldReadGoRight != file.isErrorReading();
		} catch (IOException e) {
			log.error("Error during testing", e);
		}
		return result;
	}
}
