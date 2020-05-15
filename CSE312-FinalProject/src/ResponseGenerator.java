import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * This creates the Hash to secure passwords
 * @author Illham Alam
 *
 */
public class ResponseGenerator {

	/**
	 * Creates a random secure Salt for the password.
	 * Source: https://examples.javacodegeeks.com/core-java/security/generate-a-secure-random-number-example/
	 * @return Salt 
	 */
	public static String generateSalt() {
		SecureRandom randomString = new SecureRandom();
		byte[] randomBytes = new byte[64];
		randomString.nextBytes(randomBytes);
		//System.out.println("Salt: " + Base64.getEncoder().encodeToString(randomBytes)); //Actual String
		return Base64.getEncoder().encodeToString(randomBytes);	
	}

	/**
	 * Combines the password with the salt  
	 * @param password password
	 * @param salt salt from generateSalt
	 * @return sha256 hash for the string 
	 */
	public static String generateHash(String password, String salt) {
		byte[] rawhash = null;
		try {
			rawhash = MessageDigest.getInstance("SHA-256").digest((password+salt).getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Dumb Dumb wrote the wrong algo");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Dumb Dumb used the wrong input");
			e.printStackTrace();
		}
		System.out.println("Password + Salt: " +password + salt);
		System.out.println("Password + Salt (Hashed): " + Base64.getEncoder().encodeToString(rawhash)); //Completed hash
		String hash = convertToHex(rawhash);
		System.out.println("Password + Salt (Hashed + Converted): " + hash);
		return hash;
	}

	/**
	 * Converting from a byte array to hex. 
	 * This one was a nightmare to get working. Very confusing to since there is no library to do it for us. 
	 * You would've thought we'd get to use bycript though. *sigh*
	 * Source: https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 * @param rawhash raw hash in bytes
	 * @return converted hash in hex
	 */
	private static String convertToHex(byte[] rawhash) {
		BigInteger bigInt = new BigInteger(1, rawhash);
        String hash = bigInt.toString(16);
        int paddingLength = (rawhash.length * 2) - hash.length();
        if(paddingLength > 0)
        {
            return String.format("%0" + paddingLength + "d", 0) + hash;
        }
		return hash;
	}

	

}
