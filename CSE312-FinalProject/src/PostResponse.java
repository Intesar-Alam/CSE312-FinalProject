import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is the parser for POST HTTP responses send though localhost. Use parseln(String s) to insert headers and search to search for headers.
 * @author Illham Alam
 *
 */
public class PostResponse extends Response {
	/*
	 * Headers - All located in super class
	 * requestMethod, path, httpVersion, headers
	 */
	
	
	//Image handling 
	private String fileName;
	private File img;
	private boolean bound = true;
	private ArrayList<String> rawData; 
	

	private String boundary = null;
	
	/**
	 * This is my constructor for the response received via the TCP socket
	 * @param response first line of the response
	 */
	public PostResponse(String response) {
		super(response);

		rawData = new ArrayList<String>();
	}
	
	/**
	 * Splits by key and value from each header from the HTTP request and adds it to the HashMap which can be indexed by using
	 * @param Each header field from the HTTP request. 
	 * One line at a time only.
	 * @throws IOException 
	 */
	public void parseln(String line){
		try {
			if(line.contains(": ")) {
				String[] parsed = line.split(": ");
				headers.put(parsed[0], parsed[1]);
				if(boundary == null && headers.containsKey("Content-Type")) {
					System.out.println("    Content-Type");
					String[] temp = headers.get("Content-Type").split("=----");
					boundary = temp[1];
				}
			}else if (boundary != null && line.contains(boundary)){
				//Do Nothing
			}else {
				System.out.println("ADDING 	Form Data: " + line);
				rawData.add(line);
			}

		}catch (Exception e) {
			//e.printStackTrace();
			System.out.println("CRASHED LINE WAS: " + line);
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public File getImg() {
		return img;
	}
	
	/** 
	 * Returns each form item into an arraylist
	 * @return
	 */
	public ArrayList<String> getRawData() {
		return rawData;
	}
}
