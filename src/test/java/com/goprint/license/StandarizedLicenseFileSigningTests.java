package com.goprint.license;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

public class StandarizedLicenseFileSigningTests {

	private static Logger log = Logger.getLogger(StandarizedLicenseFileSigningTests.class);

	List<String> filesInUse = new ArrayList<String>();

	@Test
	public void checkSignOKFile() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(verifySignature("OkInputSigned.txt"));
	}

	@Test
	public void checkSignOKFileMinimum() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(verifySignature("OkInputMinimumSigned.txt"));
	}

	@Test
	public void checkSignKOFileNoSign() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertFalse(verifySignature("OkInput.txt"));
	}

	@Test
	public void checkSignKOFileBadSign() {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertFalse(verifySignature("InputBadSign.txt"));
	}

	@Test
	public void doSignOKInputFile() throws IOException {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(signAndCheck("OkInputSigned.txt"));
	}

	/**
	 * Does the signing of the file to a new file and checks the generated new license
	 * 
	 * @param inputFilePath
	 *            file to sign and check
	 * @return true iff the signature verification after the signing process is OK
	 * @throws IOException
	 */
	private boolean signAndCheck(String inputFilePath) throws IOException {
		StandarizedLicenseFile file;
		file = new StandarizedLicenseFile(Constants.BASE_INPUTS + inputFilePath);
		file.signAndWriteLicenseFile(Constants.BASE_INPUTS + inputFilePath + ".out", Constants.privateKey);
		return verifySignature(inputFilePath + ".out");
	}

	@Test
	public void doSignOKMinimumInputFile() throws IOException {
		log.info("Executing test \"" + Thread.currentThread().getStackTrace()[1].getMethodName() + "\"");
		assertTrue(signAndCheck("OkInputMinimumSigned.txt"));
	}

	// FILES WERE COMPARED BECAUSE I THOUGHT SOGNING TWICE THE SAME TEXT WOULD PRODUCE AN IDENTICAL SIGNATURE
	// @Test
	// public void testCompareFiles() throws IOException {
	// assertTrue(compareFiles(Constants.BASE_INPUTS + "OkInput.txt", Constants.BASE_INPUTS + "OkInput.txt"));
	// assertFalse(compareFiles(Constants.BASE_INPUTS + "OkInput.txt", Constants.BASE_INPUTS + "OkInputMinimum.txt"));
	// }
	//
	// /**
	// * Returns true iff the contents of both files are the same byte per byte
	// *
	// * @param path1
	// * path of one of the files to compare
	// * @param path2
	// * path of the other file to compare
	// * @return true iff both files are identical
	// * @throws IOException
	// */
	// private boolean compareFiles(String path1, String path2) throws IOException {
	// FileInputStream fis1 = new FileInputStream(path1);
	// FileInputStream fis2 = new FileInputStream(path2);
	// int int1 = 0;
	// int int2 = 0;
	// while (((int1 = fis1.read()) != -1) & ((int2 = fis2.read()) != -1) && int1 == int2) {
	// ;
	// }
	// return int1 == int2;
	// }

	/**
	 * Verifies the signature of the file
	 * 
	 * @param fileName
	 * @return verification result
	 */
	private boolean verifySignature(String fileName) {
		boolean result = false;
		try {
			StandarizedLicenseFile file = new StandarizedLicenseFile(Constants.BASE_INPUTS + fileName);
			result = file.verifyLicense(Constants.publicKey);
		} catch (IOException e) {
			log.error("Error reading file", e);
		}
		return result;
	}

}
