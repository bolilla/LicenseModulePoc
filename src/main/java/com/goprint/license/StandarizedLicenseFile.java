package com.goprint.license;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

/**
 * This class contains a simplification of the contents of a license file. This is made to enable minor modifications in
 * the license file, such as case modification of the parameters, or adding trailing spaces in a line or empty lines or
 * comments.
 * 
 * @author Borja Roux
 * 
 */
public class StandarizedLicenseFile {
	private static Logger log = Logger.getLogger(StandarizedLicenseFile.class);

	private static final String LICENSE_LINES_REGEX = "";
	private static final String LICENSE_SIGNATURE_BEGIN = "===================== SIGNATURE BEGIN =====================";
	private static final String LICENSE_SIGNATURE_END = "=====================  SIGNATURE END  =====================";
	private static final String PARAM_VAL_SEPARATOR = ":";

	private static final String SIGNATURE_ALGORITHM = "SHA1withDSA";

	private static final String SIGNATURE_ALGORITHM_PROVIDER = "SUN";

	private byte readSignature[] = null;// Signature read from a file
	private StringBuffer lines = new StringBuffer();// Lines of the file as they are read

	private boolean errorReading = false;

	SortedMap<String, String> paramValues;

	/**
	 * Reads a file and creates the Standard representation of that file
	 * 
	 * @param filePath
	 *            path to the license file.
	 * @throws IOException
	 */
	public StandarizedLicenseFile(String filePath) throws IOException {
		log.debug("Standarizing license file \"" + filePath + "\"");
		paramValues = new TreeMap<String, String>();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line;
		int lineNumber = 0;
		boolean signatureReached = false;
		StringBuffer signatureCharacters = new StringBuffer();
		while (((line = br.readLine()) != null) && !errorReading) {
			log.debug(String.format("Line %03d: %s", lineNumber++, line));
			if (!signatureReached && !line.matches(LICENSE_SIGNATURE_BEGIN)) {
				lines.append(line + "\n");
			}
			line = line.trim();
			if (line.length() == 0) {// Empty line
				log.debug("Empty line. Skipping line.");
			} else if (line.startsWith("#")) {// Comment
				log.debug("This line is a comment. Skipping line.");
			} else if (signatureReached) {// Reading signature
				if (line.matches(LICENSE_SIGNATURE_END)) {// Signature ends
					log.debug("Out of signature.");
					readSignature = DatatypeConverter.parseBase64Binary(signatureCharacters.toString());
					log.debug("Signature Length: " + readSignature.length);
					signatureReached = false;
				} else {// Signature info
					log.debug("Signature data");
					signatureCharacters.append(line);
				}
			} else if (line.equalsIgnoreCase(LICENSE_SIGNATURE_BEGIN)) {// Beginning of the signature
				log.debug("Beginning of the signature");
				signatureReached = true;
				errorReading |= readSignature != null;
			} else if (!line.equalsIgnoreCase(LICENSE_LINES_REGEX)) {// Standard parameter - value line
				log.debug("Parsing parameter and value");
				errorReading = !extractParamVal(line);
			} else {// No idea what is this line about
				log.error("Cannot understand this line!!!");
				errorReading = true;
			}
		}
		errorReading = errorReading || signatureReached;
		log.debug("Finished reading file");
	}

	/**
	 * Extracts the name of the parameter and the value from the line read from the file
	 * 
	 * @param line
	 *            Read line that contains the parameter and the value
	 * @return true iff the extraction has gone OK
	 */
	private boolean extractParamVal(String line) {
		boolean result = false;
		int posSeparator = line.indexOf(PARAM_VAL_SEPARATOR);
		if (posSeparator >= 0) {
			String param = line.substring(0, posSeparator).trim().toUpperCase();
			String val = line.substring(posSeparator + 1).trim();
			log.debug("Got param \"" + param + "\" and value \"" + val + "\"");
			if (paramValues.get(param) == null) {
				paramValues.put(param, val);
				result = true;
			} else {
				log.error("Parameter \"" + param + "\" already present with value \"" + paramValues.get(param));
			}
		} else {
			log.error("Got parameter line with no parameter separator (" + PARAM_VAL_SEPARATOR + ")");
		}
		return result;
	}

	/**
	 * Signs the contents of the file read with the given private key
	 * 
	 * @param privateKey
	 *            private key to sign the contents of the file
	 * @param input
	 *            file information to sign
	 * @return true The base64 representation of the signature of the file in the input (or null if there is aproblem)
	 */
	private static byte[] getSignature(StandarizedLicenseFile input, PrivateKey privateKey) {
		byte[] result = null;
		if (privateKey == null) {
			log.error("Signing with a null key. I'm not even going to try this.");
		} else {
			try {
				log.debug("Signing license");
				byte bytesToSign[] = addElementsToSignature(input.paramValues);
				result = getSignature(bytesToSign, privateKey);
				log.debug("Signature ended: " + javax.xml.bind.DatatypeConverter.printBase64Binary(result));
			} catch (Exception e) {
				log.error("Error signing contents", e);
			}
		}
		return result;
	}

	private static byte[] getSignature(byte[] contentToSign, PrivateKey privateKey) throws NoSuchAlgorithmException,
			NoSuchProviderException, SignatureException, InvalidKeyException {
		byte[] result;
		Signature dsa = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_ALGORITHM_PROVIDER);
		dsa.initSign(privateKey);
		dsa.update(contentToSign, 0, contentToSign.length);
		result = dsa.sign();
		return result;
	}

	/**
	 * Adds all the required data to the signature
	 * 
	 * @param pv
	 *            Parameters and values to add to the signature
	 * @return the simplified for of the data to sign
	 */
	private static byte[] addElementsToSignature(SortedMap<String, String> pv) {
		StringBuffer sb = new StringBuffer();
		for (String key : pv.keySet()) {
			log.debug("Adding param \"" + new String(key.getBytes()) + "\" to the signature");
			sb.append(key);
			log.debug("Adding separator \"" + new String((PARAM_VAL_SEPARATOR.getBytes())) + "\" to the signature");
			sb.append(PARAM_VAL_SEPARATOR);
			log.debug("Adding value \"" + new String((pv.get(key) + '\n').getBytes())
					+ "\" and end of line to the signature");
			sb.append(pv.get(key) + '\n');
		}
		return sb.toString().getBytes();
	}

	/**
	 * Signs current license information and writes it to a file
	 * 
	 * @param outputLicensePath
	 *            Path to store the file
	 * @param privateKey
	 *            Key to sign the license
	 * @return true iff the sign process has been successful
	 */
	public boolean signAndWriteLicenseFile(String outputLicensePath, PrivateKey privateKey) {
		boolean result = false;
		try {
			PrintWriter pw = new PrintWriter(outputLicensePath);
			pw.write(lines.toString());
			pw.write(LICENSE_SIGNATURE_BEGIN + "\n");
			pw.write(javax.xml.bind.DatatypeConverter.printBase64Binary(getSignature(this, privateKey)) + "\n");
			pw.write(LICENSE_SIGNATURE_END + "\n");
			pw.close();
			result = true;
		} catch (FileNotFoundException e) {
			log.error("Could not find file \"" + outputLicensePath + "\"", e);
		}
		return result;
	}

	/**
	 * Verifies the signature of the read file with the given public key
	 * 
	 * @param publicKey
	 *            public key to check the signature
	 * @return true iff the signature read is not empty and is coherent with the given key
	 */
	public boolean verifyLicense(PublicKey publicKey) {
		boolean result = false;
		if (publicKey == null) {
			log.error("verifying signature of a null key. I'm not even going to try this.");
		} else if (readSignature == null) {
			log.error("Read file has no signature. Signature validation makes no sense.");
		} else {
			try {
				log.debug("Checking signature of license");
				Signature dsa = Signature.getInstance(SIGNATURE_ALGORITHM, SIGNATURE_ALGORITHM_PROVIDER);
				dsa.initVerify(publicKey);
				dsa.update(addElementsToSignature(paramValues));
				result = dsa.verify(readSignature);
				log.debug("Signature ended.");
			} catch (Exception e) {
				log.error("Error signing contents", e);
			}
		}
		return result;
	}

	/**
	 * @return the errorReading
	 */
	public boolean isErrorReading() {
		return errorReading;
	}

}
