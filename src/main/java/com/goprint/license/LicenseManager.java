package com.goprint.license;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.apache.log4j.Logger;

/**
 * This class contains the functions that manage the license related operations
 * 
 * @author Borja Roux
 * 
 */
public class LicenseManager {
	private static final String KEYSTORE_TYPE = "JKS";
	private static Logger log = Logger.getLogger(LicenseManager.class);

	/**
	 * Creates a license file given the keystore that contains the signing key (and the password), the certificate to
	 * make the signature (and the password) and the input and output files
	 * 
	 * @param keystorePath
	 *            Path to the keystore that holds the signer's certificate
	 * @param keystorePass
	 *            Passowrd to open the keystore
	 * @param privateKeyAlias
	 *            Certificate of the signer
	 * @param privateKeyPass
	 *            Password to use the certificate
	 * @param inputLicensePath
	 *            Path to the file that contains the information for the license
	 * @param outputLicensePath
	 *            Path to the file that will contain the signed license
	 * @throws IOException
	 */
	public static void createLicense(String keystorePath, String keystorePass, String privateKeyAlias,
			String privateKeyPass, String inputLicensePath, String outputLicensePath) throws IOException {
		log.debug("Creating license with parameters: keystorePath \"" + keystorePath + "\",  keystorePass \""
				+ keystorePass.replaceAll(".", "*") + "\", privateKeyAlias \"" + privateKeyAlias
				+ "\",  privateKeyPass \"" + privateKeyPass.replaceAll(".", "*") + "\",  inputLicensePath \""
				+ inputLicensePath + "\",  outputLicensePath \"" + outputLicensePath + "\"");
		StandarizedLicenseFile licenseFile = new StandarizedLicenseFile(inputLicensePath);
		licenseFile.signAndWriteLicenseFile(outputLicensePath,
				getPrivateKey(keystorePath, keystorePass, privateKeyAlias, privateKeyPass));
	}

	/**
	 * Validates a license path given a keystore (and its password), the license file to check and the certificate that
	 * has signed the license
	 * 
	 * @param keystorePath
	 *            Path to the keystore that holds the signer's certificate
	 * @param keystorePass
	 *            Passowrd to open the keystore
	 * @param licensePath
	 *            Path to the license file to verify
	 * @param certificateAlias
	 *            Certificate of the supposed signer
	 * @throws IOException
	 */
	public static void validateLicense(String keystorePath, String keystorePass, String licensePath,
			String certificateAlias) throws IOException {
		log.debug("Checking license with parameters: keystorePath \"" + keystorePath + "\",  keystorePass \""
				+ keystorePass.replaceAll(".", "*") + "\",  licensePath \"" + licensePath + "\",  certificateAlias \""
				+ certificateAlias + "\"");
		StandarizedLicenseFile licenseFile = new StandarizedLicenseFile(licensePath);
		licenseFile.verifyLicense(getPublicKey(keystorePath, keystorePass, certificateAlias));
	}

	/**
	 * Retrieves the private key from a keystore
	 * 
	 * @param keystorePath
	 *            path to the file that holds the keystore
	 * @param keystorePass
	 *            passowrd for the keystore
	 * @param certificate
	 *            alias of the private key to use
	 * @param certificatePass
	 *            passowrd for the private key
	 * @return the private key retrieved or null if a problem happens
	 */
	public static PrivateKey getPrivateKey(String keystorePath, String keystorePass, String certificate,
			String certificatePass) {
		log.debug("Getting private key: keystorePath \"" + keystorePath + "\",  keystorePass \""
				+ keystorePass.replaceAll(".", "*") + "\",  certificate \"" + certificate + "\",  certificatePass \""
				+ certificatePass.replaceAll(".", "*") + "\"");
		KeyStore ks;
		FileInputStream fis = null;
		KeyStore.PrivateKeyEntry result = null;
		try {
			ks = KeyStore.getInstance(KEYSTORE_TYPE);
			log.debug("Got empty instance of Keystore");
			fis = new java.io.FileInputStream(keystorePath);
			log.debug("Keystore file opened");
			ks.load(fis, keystorePass.toCharArray());
			log.debug("Keystore loaded:\n" + ks);
			result = (KeyStore.PrivateKeyEntry) ks.getEntry(certificate, new KeyStore.PasswordProtection(
					certificatePass.toCharArray()));
			log.debug("Got private key from keystore");
		} catch (Exception e) {
			log.error("Error geting private key", e);
		}
		return result.getPrivateKey();
	}

	/**
	 * Returns the Public key of the certificate used to sign the licenses
	 * 
	 * @param keystorePath
	 *            Path to the keystore that holds the signer's certificate
	 * @param keystorePass
	 *            Passowrd to open the keystore
	 * @param certificateAlias
	 *            Certificate of the supposed signer
	 * @return the public key of the certificate
	 */
	public static PublicKey getPublicKey(String keystorePath, String keystorePass, String certificateAlias) {
		log.debug("Getting private key: keystorePath \"" + keystorePath + "\",  keystorePass \""
				+ keystorePass.replaceAll(".", "*") + "\",  certificateAlias \"" + certificateAlias + "\"");
		KeyStore ks;
		FileInputStream fis = null;
		KeyStore.TrustedCertificateEntry result = null;
		try {
			ks = KeyStore.getInstance(KEYSTORE_TYPE);
			log.debug("Got empty instance of Keystore");
			fis = new java.io.FileInputStream(keystorePath);
			log.debug("Keystore file opened");
			ks.load(fis, keystorePass.toCharArray());
			log.debug("Keystore loaded:\n" + ks);
			result = (KeyStore.TrustedCertificateEntry) ks.getEntry(certificateAlias, null);
			log.debug("Got certificate from keystore:\n" + result);
		} catch (Exception e) {
			log.error("Error geting certificate from Keystore", e);
		}
		return result.getTrustedCertificate().getPublicKey();
	}
}
