import java.util.HashMap;

/**
 * This is the parser for GET HTTP responses send though localhost. Use parseln(String s) to insert headers and search to search for headers.
 * @author Illham Alam
 *
 */
public class GetResponse extends Response {
	/*
	 * Headers - All located in super class
	 * requestMethod, path, httpVersion, headers
	 */
	private HashMap<String, String> jquery;
	
	/**
	 * This is my constructor for a GET response received via the TCP socket
	 * @param response initial line of the response
	 */
	public GetResponse(String response) {
		super(response);
		jquery = new HashMap<String, String>();
		
		//Check for queries
		if(super.path.contains("?")) {
			String temp = super.path;
			String[] templist = temp.split("\\?"); //Separates path from query
			super.path = templist[0]; //Actual path without the query
			String[] qlist = templist[1].split("&"); //Separates all queries
			for(int i = 0; i < qlist.length; i++) {
				String[] kvlist = qlist[i].split("="); //Separates all key value pairs
				jquery.put(kvlist[0], kvlist[1]); //puts all queries in jquery
			}
		}
	}
	
	/**
	 * Looks for the query in the HTTP response and returns if it exist
	 * @param header The query thats being looked for.
	 * @return a boolean true if it exist false otherwise.
	 */
	public boolean searchQuery(String query) {
		return jquery.containsKey(query);
	}
	
	/**
	 * Looks for the query in the HTTP response and returns the value associated with it.
	 * @param header The query thats being looked for.
	 * @return The value of that header.
	 */
	public String findQuery(String header) {
		return jquery.get(header);
	}
}
