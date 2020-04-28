package User;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtils {
	// Password Encryption attributes
	private static final Random rand = new SecureRandom();
	private static final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWYZabcdefghijklmnopqrstuvwyz";
	private static final int iterations = 10000;
	private static final int key_len = 256;

	// get salt (extra layer security)
	public static String getSalt(int length) {
		StringBuilder returnValue = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			returnValue.append(alphabet.charAt(rand.nextInt(alphabet.length())));
		}
		return new String(returnValue);
	}

	public static String hash(String password, String salt) {

		char[] pass = password.toCharArray();
		byte[] newSalt = salt.getBytes();

		PBEKeySpec spec = new PBEKeySpec(pass, newSalt, iterations, key_len);
		Arrays.fill(pass, Character.MIN_VALUE);
		try {
			SecretKeyFactory secKeyFac = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			byte[] securePassword = secKeyFac.generateSecret(spec).getEncoded();
			return (Base64.getEncoder().encodeToString(securePassword));
		} catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
			System.err.println("Error while hashing a password: " + ex.getMessage());
			return password;
		} finally {
			spec.clearPassword();
		}
	}

	public static String generateSecurePassword(String myPassword, String salt) {

		String returnValue = null;
		String securePassword = hash(myPassword, salt);

		returnValue = Base64.getEncoder().encodeToString(securePassword.getBytes());
		return returnValue;
	}

	public static boolean verifyPassword (String password, String key, String salt) {
	    String optEncrypted = hash(password, salt);
	    if (!optEncrypted.equals(key)) return false;
	    return optEncrypted.equals(key);
	  }
}
