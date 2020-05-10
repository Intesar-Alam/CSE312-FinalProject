import java.util.Date;

import javax.net.ssl.SSLEngineResult.Status;

public class StatusUpdate {

	private String STATUS; 
	private String USERNAME;
	private String TIME;
	
	public StatusUpdate(String status) {
		STATUS = status; 
		USERNAME = "anonymous";
		TIME = "" + new Date();
	}
	
	public StatusUpdate(String status, String username) {
		STATUS = status; 
		USERNAME = username;
		TIME = "" + new Date();
	}
	
	public StatusUpdate(String status, String username, String time) {
		STATUS = status; 
		USERNAME = username;
		TIME = time;
	}
	
	/**
	 * Gets the full post as text
	 * @return {username} said: {status} @ {time}
	 */
	public String getPost() {
		return USERNAME + " said: " + STATUS + " @" + TIME; 
	}
	
	public String getCSV() {
		return USERNAME + "," + STATUS + "," + TIME; 
	}

	/**
	 * Get the status only
	 * @return
	 */
	public String getSTATUS() {
		return STATUS;
	}

	/**
	 * returns the username only
	 * @return
	 */
	public String getUSERNAME() {
		return USERNAME;
	}

	/**
	 * returns the time the post was made
	 * @return
	 */
	public String getTIME() {
		return TIME;
	}
	
	/**
	 * Compares to post to make sure they aren't the same
	 * @param post the other post you're comparing this one to 
	 * @return true if they are the same post
	 */
	public boolean compare(StatusUpdate update) {
		boolean notSame = false;
		if(update.getPost().equals(getPost())) notSame = true;
		return notSame;
	}
	
	
}
