import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Authenticate {

	private static ArrayList<String> userData;
	private static ArrayList<String> reqNotMet;
	
	public Authenticate(BufferedReader bufferedReader) throws IOException {
		userData = new ArrayList<String>();
		reqNotMet = new ArrayList<String>();
		
		//Start reading Input Stream
		String line = bufferedReader.readLine();
		
		//Skip first line containing GET /<path> HTTP/1.1 or POST /<path> HTTP/1.1
		line = bufferedReader.readLine();
		
		//Find Username and Password
		boolean isNotTheEnd = true;
		String tempBound = "";
		while(isNotTheEnd)
		{		
			if(line == null || line.isEmpty() || line.equals("Submit")) {
				line = bufferedReader.readLine();
			}
			else if(line.endsWith("--")) {
				isNotTheEnd = false;
			}
			else if(line.contains("boundary=")) {
				String[] tempHeaderValues = line.split("boundary=");
				tempBound = tempHeaderValues[1];
				line = bufferedReader.readLine();
			}
			else if(line.contains(": ")) {
				line = bufferedReader.readLine();
			}
			else if(line.contains(tempBound)) {
				line = bufferedReader.readLine();
			}
			else {
				userData.add(line);
				line = bufferedReader.readLine();
			}
		}
	}
	
	public ArrayList<String> getUserData(){
		return userData;
	}
	
	public boolean doesUserExist(String username, String path) throws IOException {
		return userExists(username, path);
	}
	
	public boolean isPasswordCorrect(String username, String path) throws IOException, NoSuchAlgorithmException {
		return passwordCorrect(username, path);
	}
	
	public boolean isUsernameValid(String username, String path) throws IOException {
		return usernameValid(username, path);
	}
	
	public boolean isPasswordValid(String username) throws IOException {
		return passwordValid(username);
	}
	
	public byte[] getSalt() {
		return createSalt();
	}
	
//	public String getToken(String path, String username) throws IOException {
//		return findToken(path, username);
//	}
	
	public String getSecurePassword(String password, byte[] salt) throws NoSuchAlgorithmException {
		return generateSecurePassword(password, salt);
	}
	
	public void toData(String path, ArrayList<String> data) throws IOException {
		writeToCSV(path, data);
	}
	
	public ArrayList<String> getReqNotMet(){
		return reqNotMet;
	}
	
	private static byte[] createSalt() {
		byte[] salt = new byte[16];
		SecureRandom rng = new SecureRandom();
		rng.nextBytes(salt);
		return salt;
	}
	
	private static String bytesToStringHex(byte[] hashedPassword) {
		String pString = null;
		StringBuilder sb = new StringBuilder();
        for(int i=0; i< hashedPassword.length ;i++)
        {
            sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
        }
        pString = sb.toString();
        return pString;
	}
	
	private static String generateSecurePassword(String password, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] hashedPassword = md.digest(( Base64.getEncoder().encodeToString(salt) + password).getBytes(StandardCharsets.UTF_8));
		return bytesToStringHex(hashedPassword);
	}
	
	private static boolean containsUpper(String password) {
		Pattern p = Pattern.compile("[A-Z]");
		Matcher m = p.matcher(password);
		return m.find();
	}
	
	private static boolean containsLower(String password) {
		Pattern p = Pattern.compile("[a-z]");
		Matcher m = p.matcher(password);
		return m.find();
	}
	
	private static boolean containsNumber(String password) {
		Pattern p = Pattern.compile("[0-9]");
		Matcher m = p.matcher(password);
		return m.find();
	}
	
	private static boolean containsSpecialChar(String password) {
		Pattern p = Pattern.compile("[!@#$%&*()_+=|<>?{}\\\\[\\\\]~-]");
		Matcher m = p.matcher(password);
		return m.find();
	}
	
	private static boolean usernameValid(String username, String path) throws IOException {
		reqNotMet = new ArrayList<String>();
		if(username == "") {
			reqNotMet.add("You did not enter a username. Please try again.");
			return false;
		}
		else {
			String temp = "";
			File f = new File(path);
			BufferedReader r = new BufferedReader(new FileReader(f));
			temp = r.readLine();
			while(((temp = r.readLine())) != null) {
				String[] tempList = temp.split(",");
				if(tempList[0].equals(username)) {
					reqNotMet.add("This username is already in use. Please try again.");
					return false;
				}
			}
		}
		return true;
	}
	
	private static boolean passwordValid(String password) {
		reqNotMet = new ArrayList<String>();
		if(password == "") {
			reqNotMet.add("You did not enter a password. Please try again.");
			return false;
		}
		if(password == "" || password.length() <= 7) {
			reqNotMet.add("Your password does not meet the mininmum length requirement. Please try again.");
			return false;
		}
		if(containsUpper(password) == false) {
			reqNotMet.add("Your password does not contain a uppercase letter. Please try again.");
			return false;
		}
		if(containsLower(password) == false) {
			reqNotMet.add("Your password does not contain a lowercase letter. Please try again.");
			return false;
		}
		if(containsNumber(password) == false) {
			reqNotMet.add("Your password does not contain a numerical character. Please try again.");
			return false;
		}
		if(containsSpecialChar(password) == false) {
			reqNotMet.add("Your password does not contain a special character. Please try again.");
			return false;
		}
		return true;
	}
	
	private static boolean userExists(String username, String path) throws IOException {
		String temp = "";
		File f = new File(path);
		BufferedReader r = new BufferedReader(new FileReader(f));
		while(((temp = r.readLine())) != null) {
			String[] tempList = temp.split(",");
			if(tempList[0].equals(username)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean passwordCorrect(String password, String path) throws NoSuchAlgorithmException, IOException {
		String temp = "";
		File f = new File(path);
		BufferedReader r = new BufferedReader(new FileReader(f));
		temp = r.readLine();
		while(((temp = r.readLine())) != null) {
			String[] tempList = temp.split(",");
			String hashedPassword = generateSecurePassword(password, Base64.getDecoder().decode(tempList[2]));
			if(tempList[1].equals(hashedPassword)) {
				return true;
			}
		}
		return false;
	}
	
//	private static byte[] generateToken() {
//		byte[] token = new byte[16];
//		SecureRandom rng = new SecureRandom();
//		rng.nextBytes(token);
//		return token;
//	}
	
//	private static String findToken(String path, String username) throws IOException {
//		String token = "";
//		String temp = "";
//		File f = new File(path);
//		BufferedReader r = new BufferedReader(new FileReader(f));
//		temp = r.readLine();
//		while(((temp = r.readLine())) != null) {
//			String[] tempList = temp.split(",");
//			if(tempList[0].equals(username)) {
//				token = tempList[3];
//			}
//		}
//		return token;
//	}
	
//	private static boolean checkCookieExists(String path, HashMap<String, String> requestHeader) throws IOException {
//		String token = "";
//		String temp = "";
//		File f = new File(path);
//		BufferedReader r = new BufferedReader(new FileReader(f));
//		temp = r.readLine();
//		while(((temp = r.readLine())) != null) {
//			String[] tempList = temp.split(",");
//			if(requestHeader.containsKey("Cookie") && requestHeader.get("Cookie").equals("id="+tempList[3])) {
//				data.add(tempList[0]);
//				data.add(tempList[1]);
//				data.add(tempList[2]);
//				data.add(tempList[3]);
//				return true;
//			}
//		}
//		return false;
//	}
	
	private static void writeToCSV(String path, ArrayList<String> data) throws IOException {
		FileWriter w = new FileWriter(path, true);
		w.append(data.get(0) + "," + data.get(1) + "," + data.get(2) + "," + data.get(3) + "\n");
		w.flush();
		w.close();
	}

}
