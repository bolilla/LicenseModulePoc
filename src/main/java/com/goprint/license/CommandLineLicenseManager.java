package com.goprint.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class provides a command line interface for license creation and validation
 * 
 * @author Borja Roux
 * 
 */
public class CommandLineLicenseManager {

	/**
	 * Invoke this method for interactive license creation and validation
	 * 
	 * @param args
	 *            not used
	 */
	public static void main(String[] args) throws Exception {
		boolean exit = false;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = null;
		int option = 0;
		while (!exit) {
			showMenu();
			try {
				line = br.readLine();
				option = Integer.parseInt(line);
			} catch (NumberFormatException nfe) {
				System.err.println("Error reading line \"" + line + "\". It does not seem to be a number");
			}
			switch (option) {
			case 1:
				validateLicenseOption(br);
				break;
			case 2:
				createLicenseOption(br);
				break;
			case 0:
				System.out.println("Good bye!");
				exit = true;
				break;
			default:
				System.out.println("Cannot understand option '" + option + "'");
			}
		}
	}

	/**
	 * Manages the option Create License
	 * 
	 * @param br
	 *            Command line reader
	 * @throws IOException
	 */
	private static void createLicenseOption(BufferedReader br) throws IOException {
		System.out.println("=======================");
		System.out.println("Generating license file");
		System.out.println("=======================");
		System.out.println("Please insert the full path to the keystore");
		String keystorePath = br.readLine();
		System.out.println("Please insert the password of the keystore [if any]");
		String keystorePass = br.readLine();
		System.out.println("Please insert the certificate identifier to verify the sign of the license file");
		String certificate = br.readLine();
		System.out.println("Please insert the password of the certificate for signing [if any]");
		String certificatePass = br.readLine();
		System.out.println("Please insert the full path to the input license file");
		String inputLicensePath = br.readLine();
		System.out.println("Please insert the full path to the output license file");
		String outputLicensePath = br.readLine();
		LicenseManager.createLicense(keystorePath, keystorePass, certificate, certificatePass, inputLicensePath,
				outputLicensePath);
	}

	/**
	 * Manages the option Validate License
	 * 
	 * @param br
	 *            Command line reader
	 * @throws IOException
	 */
	private static void validateLicenseOption(BufferedReader br) throws IOException {
		System.out.println("=======================");
		System.out.println("Validating license file");
		System.out.println("=======================");
		System.out.println("Please insert the full path to the keystore");
		String keystorePath = br.readLine();
		System.out.println("Please insert the password of the keystore [if any]");
		String keystorePass = br.readLine();
		System.out.println("Please insert the full path to the license file");
		String licensePath = br.readLine();
		System.out.println("Please insert the certificate identifier to verify the sign of the license file");
		String certificate = br.readLine();
		LicenseManager.validateLicense(keystorePath, keystorePass, licensePath, certificate);
	}

	/**
	 * Shows the operations available
	 */
	private static void showMenu() {
		System.out.println("Wellcome to the GoPrint license manager");
		System.out.println("");
		System.out.println("1) Validate a license");
		System.out.println("2) Create a license");
		System.out.println("0) Exit");
		System.out.println("");
	}
}
