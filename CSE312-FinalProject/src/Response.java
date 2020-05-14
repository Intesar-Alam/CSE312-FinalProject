import java.io.IOException;
import java.util.HashMap;

/**
 * This is the parser for GET and POST HTTP responses send though localhost. Use parseln(String s) to insert headers and search to search for headers.
 * @author Illham Alam
 *
 */
public class Response {
	//Headers
		protected String requestMethod;
		protected String path;
		protected String httpVersion;
		
		//Body Headers
		protected HashMap<String, String> headers;
		
//		public TCPResponse (String response, boolean typeCheck) {
//			GetResponse(response);
//		}
		
		/**
		 * Creates a generic HTTP response that does not check for Get or Post Request
		 * @param response initial line from the response
		 */
		public Response(String response) {
			String[] header = response.split(" ");
			requestMethod = header[0];
			path = header[1];
			httpVersion =header[2];
			headers =new HashMap<String, String>();
		}

		/**
		 * Splits by key and value from each header from the HTTP request and adds it to the HashMap which can be indexed by using
		 * @param Each header field from the HTTP request. 
		 * One line at a time only.
		 * @throws IOException 
		 */
		public void parseln(String line){
			try {
				String[] parsed = line.split(": ");
//				System.out.print("We added the following to the body " + parsed[0]);
//				System.out.print(" and " + parsed[1] +"\n");
				headers.put(parsed[0], parsed[1]);
//				System.out.println("The value when searched is: " + body.get(parsed[0]));
			}catch (Exception e) {
				System.out.println("Unable to add header");
				//TODO override this method in POST and when it crashes add write this as a new png file and save it.
			}
			
		}
		
		/**
		 * Looks for the header in the HTTP response and returns if it exist
		 * @param header The header thats being looked for.
		 * @return a boolean true if it exist false otherwise.
		 */
		public boolean search(String header) {
			return headers.containsKey(header);
		}
		
		/**
		 * Looks for the header in the HTTP response and returns the value associated with it.
		 * @param header The header thats being looked for.
		 * @return The value of that header.
		 */
		public String find(String header) {
			return headers.get(header);
		}
		
		//Getters and Setters for Global Variables
		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getRequestMethod() {
			return requestMethod;
		}

		@SuppressWarnings("unused")
		private void setRequestMethod(String requestMethod) {
			this.requestMethod = requestMethod;
		}

		public String getHttpVersion() {
			return httpVersion;
		}

		@SuppressWarnings("unused")
		private void setHttpVersion(String httpVersion) {
			this.httpVersion = httpVersion;
		}

		public HashMap<String, String> getHeaders() {
			return headers;
		}

		@SuppressWarnings("unused")
		private void setHeaders(HashMap<String, String> headers) {
			this.headers = headers;
		}
}
