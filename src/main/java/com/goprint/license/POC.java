package com.goprint.license;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class POC {

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

	public static void main(String[] args) throws Exception {
		Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
		for (int i = 0; i < 100; i++) {
			dsa.initSign(privateKey);
			FileInputStream fis = new FileInputStream(BASE_INPUTS + "OkInput.txt");
			BufferedInputStream bufin = new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = bufin.read(buffer)) >= 0) {
				dsa.update(buffer, 0, len);
			}
			bufin.close();
			byte[] realSig = dsa.sign();

			// ///////////////////////

			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(publicKey);
			FileInputStream datafis = new FileInputStream(BASE_INPUTS + "OkInput.txt");
			bufin = new BufferedInputStream(datafis);
			buffer = new byte[1024];
			while (bufin.available() != 0) {
				len = bufin.read(buffer);
				sig.update(buffer, 0, len);
			}
			;
			bufin.close();
			boolean verifies = sig.verify(realSig);
			System.out.println("signature verifies: " + verifies);
			System.out.println("XXXXXXXX => " + javax.xml.bind.DatatypeConverter.printBase64Binary(realSig));
		}
	}
}
