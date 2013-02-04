package com.goprint.license;

import java.security.PrivateKey;
import java.security.PublicKey;

public class Constants {

	private static final String BASE = "C:\\Users\\borja\\CustomWorkspaces\\GoPrint\\LicenseModule\\src\\test\\res\\";
	public static final String BASE_INPUTS = BASE + "inputs\\";
	private static final String keystorePath = BASE + "keystores\\GoPrintTestKeystore.jks";
	private static final String keystorePass = "GoPrint123";
	private static final String certificate = "goPrintSelfSignedCA";
	private static final String publicCertificate = "publicgoprintselfsignedca";
	private static final String certificatePass = "GoPrint123";
	public static final PrivateKey privateKey = LicenseManager.getPrivateKey(keystorePath, keystorePass, certificate,
			certificatePass);
	public static final PublicKey publicKey = LicenseManager
			.getPublicKey(keystorePath, keystorePass, publicCertificate);

}
