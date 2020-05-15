import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import java.io.FileReader;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;

public class Webserver {
	
	//Global Variables for server set up
	private static ServerSocket server;
	private static int port = 8000; //Port the server is running on.
	private static String input = ""; //Holds any data received from the client.
	private static String output = ""; //Holds any data to be sent to the client.
	private static Date timestamp; //Adds a time stamp to aid in debugging. (Please create a new date on each use.)
	
	//Global Variable for server operation
	private static InputStream inStream;
	private static BufferedReader breader; 
	//private static Scanner scaner;
	private static PrintStream stream;
	private static Response response;
	private static byte [] rawImage;
	
	//Global File Reference List
	private static HashMap<String, String> webList; 
	private static HashMap<String, String> movedList; 
	private static ArrayList<String> imageList;
	private static ArrayList<String> formList;//HW4 Objective 1
	private static File csvData = new File("src/logindat.csv");
	
	//Storing the userdata
	private static HashMap<String, String> userpass;
	private static HashMap<String, String> usersalt;
	private static HashMap<String, String> usertoke;
	private static HashMap<String, Boolean> userexit;
	
	//Change type to StatusUpdate in the future when it starts working
	private static ArrayList<StatusUpdate> publicPosts;
	
	/*
	 * HTTP Return Headers. 
	 * (Reminder when adding new headers please and "\r\n", anything after the last header should start with \r\n.)
	 */
	//HTTP Status Headers
	private final static String STATUS101 = "HTTP/1.1 101 Switching Protocols\r\n";
	private final static String STATUS200 = "HTTP/1.1 200 OK\r\n";
	private final static String STATUS201 = "HTTP/1.1 201 Document Created\r\n";
	private final static String STATUS301 = "HTTP/1.1 301 Moved Permenatly\r\n";
	private final static String STATUS401 = "HTTP/1.1 401 Not logged in\r\n";
	private final static String STATUS403 = "HTTP/1.1 403 Forbidden\r\n";
	private final static String STATUS404 = "HTTP/1.1 404 Item Not Found\r\n";
	private final static String STATUS501 = "HTTP/1.1 501 Not Implemented\r\n";
	
	//Content Type Headers 
	private final static String NO_SNIFF = "X-Content-Type-Options: nosniff\r\n";
	private final static String TEXTHTML = "Content-Type: text/html\r\n";
	private final static String TEXTJAVA = "Content-Type: text/javascript\r\n";
	private final static String TEXT_CSS = "Content-Type: text/css\r\n";
	private final static String TEXTPLAN = "Content-Type: text/plain\r\n";
	private final static String IMG__PNG = "Content-Type: image/png\r\n";
	private final static String CONTENTL = "Content-Length: ";
	private final static String LOCATION = "Location: ";
	
	
	
	
	
	
	
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
		server = new ServerSocket(port);
        System.out.println("Project server running on port: " + port);
        List<Socket> clients = new ArrayList<>();
        
		//initializes to handle pages, userdata, etc
		initalizeLists();
		csvReader();
        
//      Uncomment line below to test docker compose and mango
//		MongoClient mongo = MongoClients.create("mongodb://mongo:27017");
//		MongoClient mongo = MongoClients.create("mongodb://localhost:27017");

        
        while(true){
            try(Socket socket = server.accept())
            {
            	//Creating the input and output streams via the socket.
            	inStream = socket.getInputStream();
	            breader = new BufferedReader(new InputStreamReader(inStream));
	            stream = new PrintStream(socket.getOutputStream());
		        
	            //Reads request send from the client browser.
				readRequest();
				
				//Create an empty output. This will contain the data sent to the client.
				output = "";
				
				//Used for serving images
				boolean fileTypeIsImage = false;
				rawImage = null;
				
				//Creates a response based of the request.
				if(response.getRequestMethod().equals("GET")) fileTypeIsImage = handleGETRequest();
				else if(response.getRequestMethod().equals("POST")) fileTypeIsImage = handlePOSTRequest();
				
				//Output the return headers and content to the user.
				System.out.println("\nThe server responded with \n" + output + "\n\n");
				stream.write(output.getBytes("UTF-8")); //Converts into a byte array for the browser.
				//Images are out put differently because it is already in bytes.
				if(fileTypeIsImage) {
					stream.write(rawImage);
					fileTypeIsImage = false;
				}
				output = "";
                breader.close();
                
                /**
                 * TODO Terences code below
                 */
				
//		        ClientsInformation temp = new ClientsInformation();
//		        temp.update(breader);
//		        String request = temp.getRequest();
//		        
//		        Authenticate authenticate = new Authenticate(breader);
//		        ArrayList<String> userData = authenticate.getUserData();
//		        
//		        PrintStream ps = new PrintStream(socket.getOutputStream());
//		        OutPrintStream test = new OutPrintStream();
//
//		        HashMap<String,String> info = temp.getHashMap();
//		        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
//		        String websocketkey = "";
//		        
//			 	File folder = new File("Multimedia_Content/");
//			 	 
//
//		        HashMap<String,Integer> votes = new HashMap<String,Integer>();
//		        String[] files = folder.list();
//		        for(int i = 0; i < files.length; i++)
//		        {
//		        	votes.put(files[i], 0);
//		        }
//		        
//		        ArrayList<String> filename = new ArrayList<String>();
//		 
//		        for (String filenum : files)
//		        {
//		            filename.add(filenum);
//		        }
//		        
//		        
//		        if(info.containsKey("Sec-WebSocket-Key"))
//		        {
//		        	websocketkey = info.get("Sec-WebSocket-Key");
//		        }
//		        
//		        if((request.contains("/upvote")))
//		        {
//		        	String holder = request;
//		        	String[] tempholder = holder.split("/upvote");
//		        	int value = Integer.parseInt(tempholder[1]);
//		        	value++;
//		        	String retval = Integer.toString(value);
//		        	ps.write(retval.getBytes("UTF-8"));
//		        }
//		        if((request.contains("/downvote")))
//		        {
//		        	String holder = request;
//		        	String[] tempholder = holder.split("/downvote");
//		        	int value = Integer.parseInt(tempholder[1]);
//		        	value--;
//		        	String retval = Integer.toString(value);
//		        	ps.write(retval.getBytes("UTF-8"));
//		        }
//		        
//		        if((request.compareTo("/") == 0) || (request.compareTo("/index.html") == 0))
//			    {
//		        	String s = "public/index.html";
//					File file = new File(s);
//		 	        Scanner sc2 = new Scanner(file); 
//		 	       	String outputString = "";
//		 	        
//		 	        while (sc2.hasNextLine()) 
//		 	        {
//		 	        	String temp8 = sc2.nextLine();
//		 	        	outputString += (temp8); 
//		 	        }
//		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//		 	        ps.write(outputString.getBytes("UTF-8"));
//		 	        sc2.close();
//			    }
//		        if((request.compareTo("/login") == 0))
//		        {
//		        	String r = "";
//		        	String outputString = "";
//		        	
//		        	if(userData.isEmpty() || userData.size() != 2) {
//						r = "You must enter both fields. Please try again.";
//						outputString += "HTTP/1.1 200 OK\r\n";
//						outputString += "Content-Type: text/plain\r\n";
//						outputString += "Content-Length: " + r.length();
//						outputString += "\r\n\r\n" + r;
//					}
//		        	else {
//						if(authenticate.doesUserExist(userData.get(0), "public/accountinfo.csv")) {
//							if(authenticate.isPasswordCorrect(userData.get(1), "public/accountinfo.csv")) {
//								outputString += "HTTP/1.1 301 MOVED PERMANENTLY\r\n";
//								outputString += "Content-Type: text/html\r\n";
//								outputString += "Location: /index.html";
//								outputString += "\r\n\r\n";
//							}
//							else {
//								r = "This password is incorrect. Please try again.";
//								outputString += "HTTP/1.1 200 OK\r\n";
//								outputString += "Content-Type: text/plain\r\n";
//								outputString += "Content-Length: " + r.length();
//								outputString += "\r\n\r\n" + r;
//							}
//						}
//						else {
//							r = "This username does not exist. Please try again.";
//							outputString += "HTTP/1.1 200 OK\r\n";
//							outputString += "Content-Type: text/plain\r\n";
//							outputString += "Content-Length: " + r.length();
//							outputString += "\r\n\r\n" + r;
//						}
//					}
//		        	
//		        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//		 	        ps.write(outputString.getBytes("UTF-8"));
//		        }
//		        if((request.compareTo("/registration.html") == 0)) {
//		        	String s = "public/registration.html";
//					File file = new File(s);
//		 	        Scanner sc2 = new Scanner(file); 
//		 	       	String outputString = "";
//		 	        
//		 	        while (sc2.hasNextLine()) 
//		 	        {
//		 	        	String temp8 = sc2.nextLine();
//		 	        	outputString += (temp8); 
//		 	        }
//		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//		 	        ps.write(outputString.getBytes("UTF-8"));
//		 	        sc2.close();
//				}
//		        if((request.compareTo("/registration") == 0)) {
//		        	String r = "";
//		        	String outputString = "";
//		        	
//		        	if(userData.isEmpty() || userData.size() != 2) {
//						r = "You must enter both fields. Please try again.";
//						outputString += "HTTP/1.1 200 OK\r\n";
//						outputString += "Content-Type: text/plain\r\n";
//						outputString += "Content-Length: " + r.length();
//						outputString += "\r\n\r\n" + r;
//					}
//		        	else {
//		        		if(authenticate.isUsernameValid(userData.get(0), "public/accountinfo.csv")) {
//							if(authenticate.isPasswordValid(userData.get(1))) {
//								byte[] salt = authenticate.getSalt();
////								byte[] token = authenticate.getToken("public/accountinfo.csv");
//								String newPassword = authenticate.getSecurePassword(userData.get(1), salt);
//								userData.set(1, newPassword);
//								userData.add(Base64.getEncoder().encodeToString(salt));
////								userData.add(Base64.getEncoder().encodeToString(token));
//								authenticate.toData("public/accountinfo.csv", userData);
//								
//								outputString += "HTTP/1.1 301 MOVED PERMANENTLY\r\n";
//								outputString += "Content-Type: text/html\r\n";
//								outputString += "Location: /index.html";
//								outputString += "\r\n\r\n";
//							}
//							else {
//								r = "";
//								ArrayList<String> t = authenticate.getReqNotMet();
//								for(int i = 0; i < t.size(); i++) {
//									r = r + t.get(i) + "\r\n";
//								}
//								outputString += "HTTP/1.1 200 OK\r\n";
//								outputString += "Content-Type: text/plain\r\n";
//								outputString += "Content-Length: " + r.length();
//								outputString += "\r\n\r\n" + r;
//							}
//						}
//		        		else {
//		        			r = "";
//							ArrayList<String> t = authenticate.getReqNotMet();
//							for(int i = 0; i < t.size(); i++) {
//								r = r + t.get(i) + "\r\n";
//							}
//							outputString += "HTTP/1.1 200 OK\r\n";
//							outputString += "Content-Type: text/plain\r\n";
//							outputString += "Content-Length: " + r.length();
//							outputString += "\r\n\r\n" + r;
//						}
//		        	}
//		        	
//		        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//		 	        ps.write(outputString.getBytes("UTF-8"));
//				}
//		        if((request.compareTo("/basic.css") == 0))
//		        {
//		        	File file = new File("public/basic.css");
//		        	
//	 	        	Scanner sc2 = new Scanner(file); 
//	 	        	String outputString = "";
//	 	        	
//	 	        	while (sc2.hasNextLine()) 
//	 	        	{
//	 	        		String temp8 = sc2.nextLine();
//	 	        		outputString += (temp8); 
//	 	        	}
//	 	        	
//	 	        	test.printStreamGoodCSS(ps, outputString.getBytes("UTF-8").length);
//	 	        	ps.write(outputString.getBytes("UTF-8"));
//	 	        	sc2.close();
//		        }
//		        if((request.compareTo("/home.html") == 0))
//		        {
//		        	File file = new File("public/home.html"); 
//	 	        	Scanner sc2 = new Scanner(file); 
//	 	        	String outputString = "";
//	 	        	
//	 	        	while (sc2.hasNextLine()) 
//	 	        	{
//	 	        		String temp8 = sc2.nextLine();
//	 	        		outputString += (temp8); 
//	 	        	}
//	 	        	
//	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//	 	        	ps.write(outputString.getBytes("UTF-8"));
//	 	        	sc2.close();
//		        }
//		        if((request.compareTo("/profile.html") == 0))
//		        {
//		        	File file = new File("public/profile.html"); 
//		        	Scanner sc2 = new Scanner(file);
//				 	String outputString = "";
//					while (sc2.hasNextLine()) 
//					{
//						String temp8 = sc2.nextLine();
//						if(temp8.contains("{{ personPosts }}"))
//						{
//							for(int i = 0; i < filename.size(); i++)
//							{
//								outputString += "<img src=\"" + filename.get(i) + "\"" + "    <li class=\"vote\">\r\n" + 
//										"      <button class=\"upclick\" onclick=\"upvote(" +  i + ")\">up Vote</button>\r\n" + 
//										"      <span id=\"currVotes" + i + "\" id=>" + votes.get(filename.get(i)) +"</span>\r\n" + 
//										"      <button class=\"downclick\" onclick=\"downvote(" + i + ")\">down Vote</button>\r\n" + 
//										"      <a>{{ title }}</a>\r\n" + 
//										"    </li>";
//							}
//						}
//						else
//							outputString += (temp8) + "\r\n"; 
//					}
//		 	        
//		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//		 	        ps.write(outputString.getBytes("UTF-8"));
//		 	        sc2.close();
//		        }
//		        
//		        /*
//		         * For the multimedia makes public post
//		         * 
//		         * TODO add to document when post command is working 
//		         */
//		        if(request.compareTo("/addStatus") == 0){
//
//		        /** TODO make sure data is added **/
//		        	String idx = "publicFiles/info.txt";
//					String fileData = readTextData2(idx);
//		        	String outputString = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n\r\n";
//		        	outputString = "Content-Length: " + fileData.length() + "\r\n\r\n" + fileData;
//					ps.write(outputString.getBytes("UTF-8"));
//		        }
//		        
//		        /*
//		         * To request all posts
//		         */
//		        if(request.compareTo("/statusUpdate") == 0) {
//		        	String idx = "public/posts.txt";
//					String fileData = readTextData2(idx);
//		        	String outputString = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n";
//					outputString += "Content-Length: " + (fileData.length()) +"\r\n\r\n" + fileData;
//					ps.write(outputString.getBytes("UTF-8"));
//		        }
//		        
//		        for(int i = 0; i < filename.size(); i++)
//		        {
//		        	if(request.contains(filename.get(i)))
//		        	{
//			        	String tempstester[] = request.split("/");
//						String actualrequest2 = tempstester[1];
//						
//			            File image = new File("Multimedia_Content/" + actualrequest2);
//						if(!image.exists())
//						{
//							ps.write("Image file name does not exist".getBytes("UTF-8"));
//						}
//						else
//						{
//					        BufferedImage bufferimage = ImageIO.read(image);
//				            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//				            ImageIO.write(bufferimage, "png", byteArrayOutputStream);
//		
//				            int size = byteArrayOutputStream.toByteArray().length;
//				            
//				            test.printStreamGoodimg(ps, size);
//				            
//				            ps.write(byteArrayOutputStream.toByteArray());
//				            
//				            ps.flush();
//						}
//		        	}
//		        }
//		        if((request.compareTo("friends.html") == 0)) {
//		        	File csvfile = new File("public/accountinfo.csv");
//		        	Scanner scan = new Scanner(csvfile);
//		        	String usernames = "These are our other users </br>";
//		        	
//		        	while (scan.hasNextLine()) {
//		        		String accountinfo = scan.nextLine();
//		        		String [] ainfo = accountinfo.split(",");
//		        		usernames = usernames + ainfo[0] + "</br>";
//		        	}
//		        	
//		        	File file = new File("public/friends.html"); 
//	 	        	Scanner sc2 = new Scanner(file); 
//	 	        	String outputString = "";
//	 	        	
//	 	        	while (sc2.hasNextLine()) 
//	 	        	{
//	 	        		String temp8 = sc2.nextLine();
//	 	        		if (temp8.contains("{{ add friends }}")) {
//	 	        			temp8.replace("{{ add friends }}", usernames);
//	 	        		}
//	 	        		outputString += (temp8); 
//	 	        	}
//	 	        	
//	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//	 	        	ps.write(outputString.getBytes("UTF-8"));
//	 	        	sc2.close();
//		        }
//		        if((request.compareTo("/dmtemplate.html") == 0))
//		        {
//		        	File file = new File("public/dmtemplate.html"); 
//	 	        	Scanner sc2 = new Scanner(file); 
//	 	        	String outputString = "";
//	 	        	
//	 	        	while (sc2.hasNextLine()) 
//	 	        	{
//	 	        		String temp8 = sc2.nextLine();
//	 	        		outputString += (temp8); 
//	 	        	}
//	 	        	
//	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
//	 	        	ps.write(outputString.getBytes("UTF-8"));
//	 	        	sc2.close();
//		        }
//		        if((request.compareTo("/script2.js") == 0))
//		        {
//		        	File file = new File("public/script2.js"); 
//	 	        	Scanner sc2 = new Scanner(file); 
//	 	        	String outputString = "";
//	 	        	
//	 	        	while (sc2.hasNextLine()) 
//	 	        	{
//	 	        		String temp8 = sc2.nextLine();
//	 	        		outputString += (temp8); 
//	 	        	}
//	 	        	
//	 	        	test.printStreamGoodJS(ps, outputString.getBytes("UTF-8").length);
//	 	        	ps.write(outputString.getBytes("UTF-8"));
//	 	        	sc2.close();
//		        }
//		        if((request.compareTo("/mountain.jpeg") == 0))
//		        {
//		        	String tempstest[] = request.split("/");
//					String actualrequest = tempstest[1];
//					
//		            File image = new File("public/" + actualrequest);
//					if(!image.exists())
//					{
//						ps.write("Image file name does not exist".getBytes("UTF-8"));
//					}
//					else
//					{
//				        BufferedImage bufferimage = ImageIO.read(image);
//			            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			            ImageIO.write(bufferimage, "jpg", byteArrayOutputStream);
//	
//			            int size = byteArrayOutputStream.toByteArray().length;
//			            
//			            test.printStreamGoodimg(ps, size);
//			            
//			            ps.write(byteArrayOutputStream.toByteArray());
//			            
//			            ps.flush();
//					}
//		        }
//		        if((request.compareTo("/socket") == 0))
//		        {
//		            clients.add(socket);
//		            
//		        	byte[] response = ("HTTP/1.1 101 Switching Protocol\r\n" + 
//				        "Connection: Upgrade\r\n" + 
//				        "Upgrade: websocket\r\n" + 
//				        "Sec-WebSocket-Accept: " +
//				        Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((websocketkey + GUID).getBytes("UTF-8"))) + 
//				        "\r\n\r\n").getBytes("UTF-8");
//		        	ps.write(response);
//		        }
//		        
//		        for(int i = 0; i < clients.size(); i++)
//		        {
//		            Websocket temptest = new Websocket();
//		            
//		            temptest.socket(clients.get(i));
//		        }
//		        
//		        ps.close();
		        breader.close();
		        inStream.close();
            }
        }
	}


	/**
	 * Alam HW8
	 * Reads the Request from the scanner and parses it. 
	 * It does not return due to the use of global variables in the WebServer Class.
	 * @throws IOException 
	 */
	private static void readRequest() throws IOException {
		//Reads the first line. Of the Get Request (ie. "GET /index HTTP/1.1")
		input = breader.readLine();
		System.out.println("\nThe client submitted:\n" + input); 
		if (input == null) {
			System.out.println("\nThe System failed due to a null request\n");
			response = new GetResponse("GET /404 HTTP/1.1");
		}else if(input.contains("GET")) {
			makeGETResponse();
		}else if(input.contains("POST")) {
			makePOSTResponse();
		}else {
			System.out.println("\nThe System failed to detect a GET or POST request\n");
			response = new GetResponse("GET /404 HTTP/1.1");
		}
	}

	/**
	 * Alam HW5
	 * Parses the clients request so long it is a GET request
	 * @throws IOException 
	 */
	private static void makeGETResponse() throws IOException {
		response = new GetResponse(input);
		
		//Adds additionally headers so long they exist (ie. "Host: localhost:8000" or "Cookie: user=visited")
		boolean headerExist = true; 
		while(headerExist) {
			String line = breader.readLine();
			System.out.println(line);
			
			//If the line exist we parse it in the response class. Other we break from the loop.
			if(line.isEmpty()) headerExist = false;
			else response.parseln(line);
		}
		
	}

	/**
	 * Alam HW8
	 * Parses the clients request so long it is a POST request
	 * @throws IOException 
	 */
	private static void makePOSTResponse() throws IOException {
		response = new PostResponse(input);
		
		//Adds additionally headers so long they exist (ie. "Host: localhost:8000" or "Cookie: user=visited")
		boolean headerExist = true; 
		while(headerExist) {
			String line = breader.readLine();
			System.out.println(line);
			//If the line exist we parse it in the response class. If not we skip, if it ends with -- were done.
			if(line.isEmpty()) {
//				scaner.nextLine();
			}else if(line.endsWith("--")){
//				response.parseln(line);
				headerExist = false;
			}else {
				response.parseln(line);
			}
			
		}
		//addToDocument(((PostResponse) response).getRawData(), csvData);

	}
	
	/**
	 * Alam HW5
	 * Handles GET request based on the type and destination.
	 * Note all files must be in publicFiles to work!
	 * @return true if it is handling an image false otherwise.
	 * @throws IOException If you give it a path that does not exist
	 * @throws NoSuchAlgorithmException 
	 */
	private static boolean handleGETRequest() throws IOException, NoSuchAlgorithmException{
		String path = response.getPath();
		System.out.println("The path being searched for is :" + path);
		
		//All 200 properly typed outpaths
		if(path.endsWith(".html")) {
			String fileData = "";
			if(webList.containsKey(path)) {
				output += STATUS200 + TEXTHTML + NO_SNIFF;
				boolean session = cookieCheck();
				System.out.println("session is " + session);
				if(session && path.equals("/index.html")) {
					String user = findUserFromCookie();
					fileData = readFileTemp("public/home.html", "{{ User }}", user);
				}else {
					fileData = readFileData(webList.get(path));
				}
				output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
				return false;
			}
		
		//All images let the browser sniff the image out for convenience	
		}else if (path.endsWith(".png") || path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif")){
			output += STATUS200 + IMG__PNG; //+ NO_SNIFF;
			rawImage = readImageData(path);
			output += CONTENTL + rawImage.length + "\r\n\r\n";
			return true;
		
		//All javascript files
		}else if (path.endsWith(".js")) {
			output += STATUS200 + TEXTJAVA + NO_SNIFF; 
			String fileData = readFileData("public" + path);
			output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			return false;
		
		//All css files 
		}else if (path.endsWith(".css")){
			output += STATUS200 + TEXT_CSS + NO_SNIFF;
			String fileData = readFileData("public" + path);
			output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			return false;
			
		//All paths that are not typed properly. These only work on key words ie. /1, /home, /page2
		}else if (movedList.containsKey(path)) {
			System.out.println("This is where the 301 redirects to: " + movedList.get(path));
			output += STATUS301;// + TEXTHTML + NO_SNIFF;
			output += LOCATION + movedList.get(path); 
			return false;
			
		}
		//When none of the paths work the browser displays a 404 error.
		output += STATUS404 + TEXTHTML + NO_SNIFF;
		String fileData = readFileData("public/notFound.html");
		output += CONTENTL + fileData.length() +"\r\n\r\n" + fileData;
		return false;
	}


	/**
	 * Alam HW8
	 * Handles POST request based on the type and destination.
	 * @return true if it is handling an image false otherwise.
	 * @throws IOException 
	 */
	private static boolean handlePOSTRequest() throws IOException {
		String path = response.getPath();
		String fileData = "";
		boolean attempt = true;
		
		
		System.out.println("The path being searched for is :" + path);
		if(path.endsWith("/registration")) {
			String warning = "Account failed: ";
			ArrayList<String> up = ((PostResponse) response).getRawData();
			System.out.println("!!!!!up.size() is " + up.size() + " !!!!");
			if (up.size() != 2) { 
				attempt = false; 
				warning += "It did not contain all required fields. ";
			}else{
				if(up.get(1).length() < 8) {
					attempt = false; 
					warning += "The password is too short. ";
				}if(!Authenticate.containsLcase(up.get(1))) {
					attempt = false; 
					warning += "The password doesn't contain a lowercase. ";
				}if(!Authenticate.containsUcase(up.get(1))) {
					attempt = false; 
					warning += "The password doesn't contain an uppercase. ";
				}if(!Authenticate.containsNum(up.get(1))) {
					attempt = false; 	
					warning += "The password doesn't contain a number. ";
				}if(!Authenticate.containsSpecial(up.get(1))) {
					attempt = false; 
					warning += "The password doesn't contain a special character. ";
				}if(userpass.containsKey(up.get(0))) {
					attempt = false; 
					warning += "The username is already teken. ";
				}
			}
			
			if(attempt) {
				String user = up.get(0);
				String pass = up.get(1);
				String salt = ResponseGenerator.generateSalt();
				String saltedPass = ResponseGenerator.generateHash(pass, salt);
				String token = ResponseGenerator.generateSalt();
				userpass.put(user, saltedPass);
				usersalt.put(user, salt);
				usertoke.put(user, token);
				userexit.put(user, false);
				csvWriter();
				
				output += STATUS200 + TEXTHTML + NO_SNIFF; 
				fileData = readFileData("public/index.html");
				fileData = fileDataAddAlert(fileData, "Account created, please login now!");
				output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			}else {
				output += STATUS200 + TEXTHTML + NO_SNIFF; 
				fileData = readFileData("public/registration.html");
				fileData = fileDataAddAlert(fileData, warning);
				output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			}
			return false;
			
		}else if(path.endsWith("/login")) {
			String warning = "Incorrect login please try again: ";
		
			ArrayList<String> up = ((PostResponse) response).getRawData();
			System.out.println("!!!!!up.size() is " + up.size() + " !!!!");
			if (up.size() != 2) { 
				attempt = false; 
				warning += "It did not contain all required fields. ";
			}else {
				String user = up.get(0);
				String pass = up.get(1);
				if (!userpass.containsKey(user)) {
					attempt = false; 
					warning += "Username does not exist. ";
				}else{
					String salt = usersalt.get(user);
					String hash = ResponseGenerator.generateHash(pass, salt);
					System.out.println("\n  Log in hash: " + hash + "\n  Stored hash: " + userpass.get(user));
					if(!(userpass.get(user).equals(hash))) {
						System.out.println("submitted and stored hash don't match");
						attempt = false; 
						warning += "Password is incorrect. ";
					}else{
						System.out.println("submitted and stored hash do match!");
					}
				}
			}
			
			if(attempt) {
				output += STATUS200 + TEXTHTML + NO_SNIFF; 
				fileData = readFileTemp("public/home.html", "{{ User }}", up.get(0) );
				output += "Set-Cookie: user=" + usertoke.get(up.get(0)) + ";\r\n";
				output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			}else {
				output += STATUS200 + TEXTHTML + NO_SNIFF; 
				fileData = readFileData("public/index.html");
				fileData = fileDataAddAlert(fileData, warning);
				output += CONTENTL + fileData.length() + "\r\n\r\n" + fileData;
			}
			
		}

		output += STATUS501 + TEXTHTML + NO_SNIFF;
		output += "\r\n <h1>Error 501</h1><br /><b>Looks like you're in the future buddy.<br />This functionality hasn't been implemented yet, sorry :(</b><br />"
		+ "Page not created on server: " + port+ " at: " + timestamp;
		return false;
	}

	/**
	 * Alam HW8 
	 * Checks to see if a valid cookie exists
	 * @return true if found in the CSV
	 */
	private static boolean cookieCheck() {
		if(response.search("Cookie")) {
			System.out.println("A cookie exists");
			String temp = response.find("Cookie");
			String[] split = temp.split("=");
			String token = split[1] + "==";
			System.out.println("The token is " + token);
			if(usertoke.containsValue(token)) {
				System.out.println("And the token should be valid");;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Alam HW8
	 * Finds the user associated with a cookie 
	 * @param cookie 
	 * @return user that the cookie belongs too
	 */
	private static String findUserFromCookie() {
		String user = "";
		String temp = response.find("Cookie");
		String[] split = temp.split("=");
		String token = split[1] + "==";
		
		for(String s: usertoke.keySet()) {
			if(usertoke.get(s).equals(token)) {
				user = s;
				break;
			}
		}
		return user;
	}
	
	/**
	 * Alam HW5
	 * Reads the HTML/CSS/JS Files and puts it into a string.
	 * @param Name of the files that needs to be converted to a String.
	 * @return Converted file as a string.
	 * @throws IOException 
	 */
	private static String readFileData(String index) throws IOException {
		//Creates a new BufferedReader which consists of a FileReader with the File which contains the index of the file;
		BufferedReader fileReader = new BufferedReader(new FileReader(new File(index)));
		String fileData = "";
		String temp = "";
		//Reads each line of the file and adds it to fileData
		while((temp = fileReader.readLine())!= null) {
			fileData += temp;
		}
		fileReader.close();
		return fileData;
	}
	
	/**
	 * Alam HW8
	 * Reads the file but also replaces the key with the replacement.
	 * @param index Name of the file that needs to be converted to a string.
	 * @param key the term to be replaced.
	 * @param replace the new term to take its place.
	 * @return Converted files as a string with modifications
	 * @throws IOException
	 */
	private static String readFileTemp(String index, String key, String replace) throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(new File(index)));
		String fileData = "";
		String temp = "";
		//Reads each line of the file and adds it to fileData
		while((temp = fileReader.readLine())!= null) {
			temp = temp.replace(key, replace);
			fileData += temp;
		}
		fileReader.close();
		return fileData;
	}
	
	
	/**
	 * Alam HW8
	 * Adds an alert to the webpage when it loads
	 * @param fileData file to be modified 
	 * @param statement alert statement
	 * @return webpage with alert
	 */
	private static String fileDataAddAlert (String fileData, String statement) {
		String alert = "<script> function onload(){ alert(\"";
		alert += statement;
		alert += "\");}</script><body onload = \"onload()\">";
		fileData = fileData.replace("<body>", alert);
		return fileData;
	}

	/**
	 * From Alam HW5
	 * Reads the .txt Files and puts it into a string. Line by line
	 * @param Name of the files that needs to be converted as a String.
	 * @return Converted file as a string.
	 * @throws IOException
	 */
	private static String readTextData2(String index) throws IOException {
		//Creates a new BufferedReader which consists of a FileReader with the File which contains the index of the file;
		BufferedReader fileReader = new BufferedReader(new FileReader(new File(index)));
		String fileData = "";
		String displayTxt = "";
		String rawTxt = "";
		//Reads each line of the file and adds it to fileData
		while((rawTxt = fileReader.readLine())!= null) {
			String[] rawPost = rawTxt.split(",");
			StatusUpdate prepPost = new StatusUpdate(rawPost[0],rawPost[1],rawPost[2]); 
			displayTxt = prepPost.getPost();
			displayTxt = displayTxt.replace("&", "&amp");
			displayTxt = displayTxt.replace("<", "&lt");
			displayTxt = displayTxt.replace(">", "&gt");
			fileData += displayTxt + "<br />" + 
					"<div class=\"vote\">\r\n" + 
					"	<button class=\"upclick\" onclick=\"upvote\">up Vote</button>\r\n" + 
					"	<span class=\"currVotes\">{{ votes }}</span>\r\n" + 
					"	<button class=\"downclick\" onclick=\"downvote\">down Vote</button>\r\n" + 
					"	<a>{{ title }}</a>\r\n" + 
					"</div>";
			
		}
		fileReader.close();
		return fileData;
	}
	
	/**
	 * Alam HW5
	 * Reads and Looks for an image in the images folder.
	 * Source: https://www.tutorialspoint.com/How-to-convert-Image-to-Byte-Array-in-java
	 * 
	 * @param string Name of image.
	 * @return the image written as a string.
	 * @throws IOException 
	 */
	private static byte[] readImageData(String index) throws IOException {
		//Check the image type if the image type is not found then send the 404 error
		String type = "";
		if(index.endsWith("png")) {type = "png";}
		else if (index.endsWith("jpg")) {type = "jpg";}
		else if (index.endsWith("jpeg")) {type = "jpeg";}
		else if (index.endsWith("gif")) {type = "gif";}
		else {index = "Multimedia_Content/img404.png"; type = "png";}
		
		//check to see if image exist in imageList
		if(imageList.contains(index)) {index = "img404.png"; type = "png";}
		
		//Turns the image 
		BufferedImage bufferedReader = ImageIO.read(new File("Multimedia_Content/" + index));
		ByteArrayOutputStream imageOutPut = new ByteArrayOutputStream();
		ImageIO.write(bufferedReader, type , imageOutPut); 
		return imageOutPut.toByteArray();
	}
	
	/**
	 * From Alam HW5
	 * Method takes a string and adds it into a new document as a new line.
	 * @param String to be added to the document?
	 * @param The file that the string is being added to 
	 * @throws IOException 
	 */
	private static void addToDocument(String string, File txtFile) throws IOException {
		formList.add(string);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile));
		for(String s : formList) {
			bufferedWriter.write(s);
			bufferedWriter.newLine();
		}
		bufferedWriter.flush();
		bufferedWriter.close();
		
	}
	
	/**
	 * From Alam HW 5
	 * Method takes an array and adds it into a new document each item in the array is it's own line.
	 * @param Array to be added to the document?
	 * @param The file that the array is being added to 
	 * @throws IOException 
	 */
	private static void addToDocument(ArrayList<String> stringList, File txtFile) {
		for (String string: stringList) {
			try {
				addToDocument(string, txtFile);
			} catch (IOException e) {
				System.out.println("Could not write " + string + " to the history");
			}
		}
	}
	
	/**
	 * Alam HW8
	 * Stores all csv data into the hash maps prom previous session.
	 * @throws IOException 
	 */
	private static void csvReader() throws IOException {
		BufferedReader fileReader = new BufferedReader(new FileReader(csvData));
		String temp = "";
		System.out.println("\nAdding user data locally");
		while((temp = fileReader.readLine())!= null && !temp.isEmpty()) {
			String[] parsed = temp.split(",");
			userpass.put(parsed[0], parsed[1]);
	    	usersalt.put(parsed[0], parsed[2]);
	    	usertoke.put(parsed[0], parsed[3]);
	    	userexit.put(parsed[0], false);
	    	System.out.println("  Added: " + parsed[0] + ", " + parsed[1] + ", " +parsed[2] + ", " + parsed[3] );
		}
		fileReader.close();
	}
	
	/**
	 * Alam HW8
	 * Writes all csv files from the hashmaps from previous 
	 * @throws IOException 
	 */
	private static void csvWriter() throws IOException {
		//csvReader();
		for(String user: userpass.keySet()) {
			if (!userexit.get(user)){
				String temp = user + "," + userpass.get(user) + "," + usersalt.get(user) + "," + usertoke.get(user);
				addToDocument(temp, csvData);
				userexit.put(user, true);
				System.out.println("now added user: " + user);
			}else {
				System.out.println("user " + user + " was already added");
			}
		}
		
	}
	
	/**
	 * Alam HW 8
	 * Initializes all the key search words for the pages stored on the server.
	 */
	private static void initalizeLists() {
		//Known websites
		webList = new HashMap<String, String>();
		webList.put("/index.html","public/index.html");
		webList.put("/home.html","public/home.html");
		webList.put("/registration.html","public/registration.html");
		webList.put("/dmtemplate.html","public/dmtemplate.html");
		webList.put("/friends.html","public/friends.html");	
		webList.put("/messages.html","public/messages.html");
		webList.put("/profile.html","public/profile.html");
		
		//Redirects
		movedList = new HashMap<String, String>();
		movedList.put("/","/index.html");
		movedList.put("/page1","index.html");
		movedList.put("/index","/index.html");
		movedList.put("/1","/index.html");
		movedList.put("/home","/home.html");
		movedList.put("/register","/registration.html");
		movedList.put("/reg","/registration.html");
		movedList.put("/signup","/registration.html");
		movedList.put("/r","/registration.html");
		
		//Known images
		imageList = new ArrayList<String>();
		imageList.add("charicon.jpg");
		imageList.add("duck.png");
		imageList.add("facebook.png");
		imageList.add("road.jpeg");
		
		//User data
		formList = new ArrayList<String>();
        publicPosts = new ArrayList<StatusUpdate>();
        userpass = new HashMap<String, String>();
    	usersalt = new HashMap<String, String>();
    	usertoke = new HashMap<String, String>();
    	userexit = new HashMap<String, Boolean>();
		
	}
	
}
