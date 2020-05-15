import java.util.Date;

import javax.net.ssl.SSLEngineResult.Status;

public class StatusUpdate {

	private String STATUS; 
	private String USERNAME;
	private String TIME;
	
	/**
	 * Just a post from anonymous 
	 * @param status
	 */
	public StatusUpdate(String status) {
		STATUS = status; 
		USERNAME = "anonymous";
		TIME = "" + new Date();
	}
	
	/**
	 * A new post from a user 
	 * @param status
	 * @param username
	 */
	public StatusUpdate(String status, String username) {
		STATUS = status; 
		USERNAME = username;
		TIME = "" + new Date();
	}
	
	/**
	 * An existing post from a user
	 * @param status
	 * @param username
	 * @param time
	 */
	public StatusUpdate(String status, String username, String time) {
		STATUS = status; 
		USERNAME = username;
		TIME = time;
	}
	
	/**
	 * Generates the post as text
	 * @return {username} said: {status} @ {time}
	 */
	public String getPost() {
		return USERNAME + " said: " + STATUS + " @" + TIME; 
	}
	
	/**
	 * Generates the post as text with the oting feature
	 * TODO Terence if you can fix this please
	 * @return {username} said: {status} @ {time} as well as the voting bar
	 */
	public String getFullPost() {
		return  "<br />" +
				"<li class=\"vote\">\r\n" + 
				getPost() +
				"<hr />" +
				"      <button class=\"upclick\" onclick=\"upvote(1)\">up Vote</button>\r\n" + 
				"      <span id=\"currVotes {{ votes }} \"></span>\r\n" + 
				"      <button class=\"downclick\" onclick=\"downvote(1)\">down Vote</button>\r\n" + 
				"      <a>{{ title }}</a>\r\n" + 
				"    </li>" +
				"<br />";
	}
	
	/**
	 * get the post as a CSV
	 * @return {username},{status},{time}
	 */
	public String getCSV() {
		return STATUS + "," + USERNAME + "," + TIME; 
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
