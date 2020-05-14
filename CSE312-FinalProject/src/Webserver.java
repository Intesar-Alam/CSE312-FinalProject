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
	private static Scanner scaner;
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
            	//Creating the input and streams via the socket.
            	inStream = socket.getInputStream();
	            breader = new BufferedReader(new InputStreamReader(inStream));
		        
	            //Reads request send from the client browser.
				readRequest();
				
		        ClientsInformation temp = new ClientsInformation();
		        temp.update(breader);
		        String request = temp.getRequest();
		        
		        Authenticate authenticate = new Authenticate(breader);
		        ArrayList<String> userData = authenticate.getUserData();
		        
		        PrintStream ps = new PrintStream(socket.getOutputStream());
		        OutPrintStream test = new OutPrintStream();

		        HashMap<String,String> info = temp.getHashMap();
		        String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		        String websocketkey = "";
		        
			 	File folder = new File("Multimedia_Content/");
			 	 

		        HashMap<String,Integer> votes = new HashMap<String,Integer>();
		        String[] files = folder.list();
		        for(int i = 0; i < files.length; i++)
		        {
		        	votes.put(files[i], 0);
		        }
		        
		        ArrayList<String> filename = new ArrayList<String>();
		 
		        for (String filenum : files)
		        {
		            filename.add(filenum);
		        }
		        
		        
		        if(info.containsKey("Sec-WebSocket-Key"))
		        {
		        	websocketkey = info.get("Sec-WebSocket-Key");
		        }
		        
		        if((request.contains("/upvote")))
		        {
		        	String holder = request;
		        	String[] tempholder = holder.split("/upvote");
		        	int value = Integer.parseInt(tempholder[1]);
		        	value++;
		        	String retval = Integer.toString(value);
		        	ps.write(retval.getBytes("UTF-8"));
		        }
		        if((request.contains("/downvote")))
		        {
		        	String holder = request;
		        	String[] tempholder = holder.split("/downvote");
		        	int value = Integer.parseInt(tempholder[1]);
		        	value--;
		        	String retval = Integer.toString(value);
		        	ps.write(retval.getBytes("UTF-8"));
		        }
		        
		        if((request.compareTo("/") == 0) || (request.compareTo("/index.html") == 0))
			    {
		        	String s = "public/index.html";
					File file = new File(s);
		 	        Scanner sc2 = new Scanner(file); 
		 	       	String outputString = "";
		 	        
		 	        while (sc2.hasNextLine()) 
		 	        {
		 	        	String temp8 = sc2.nextLine();
		 	        	outputString += (temp8); 
		 	        }
		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
		 	        ps.write(outputString.getBytes("UTF-8"));
		 	        sc2.close();
			    }
		        if((request.compareTo("/login") == 0))
		        {
		        	String r = "";
		        	String outputString = "";
		        	
		        	if(userData.isEmpty() || userData.size() != 2) {
						r = "You must enter both fields. Please try again.";
						outputString += "HTTP/1.1 200 OK\r\n";
						outputString += "Content-Type: text/plain\r\n";
						outputString += "Content-Length: " + r.length();
						outputString += "\r\n\r\n" + r;
					}
		        	else {
						if(authenticate.doesUserExist(userData.get(0), "public/accountinfo.csv")) {
							if(authenticate.isPasswordCorrect(userData.get(1), "public/accountinfo.csv")) {
								outputString += "HTTP/1.1 301 MOVED PERMANENTLY\r\n";
								outputString += "Content-Type: text/html\r\n";
								outputString += "Location: /index.html";
								outputString += "\r\n\r\n";
							}
							else {
								r = "This password is incorrect. Please try again.";
								outputString += "HTTP/1.1 200 OK\r\n";
								outputString += "Content-Type: text/plain\r\n";
								outputString += "Content-Length: " + r.length();
								outputString += "\r\n\r\n" + r;
							}
						}
						else {
							r = "This username does not exist. Please try again.";
							outputString += "HTTP/1.1 200 OK\r\n";
							outputString += "Content-Type: text/plain\r\n";
							outputString += "Content-Length: " + r.length();
							outputString += "\r\n\r\n" + r;
						}
					}
		        	
		        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
		 	        ps.write(outputString.getBytes("UTF-8"));
		        }
		        if((request.compareTo("/registration.html") == 0)) {
		        	String s = "public/registration.html";
					File file = new File(s);
		 	        Scanner sc2 = new Scanner(file); 
		 	       	String outputString = "";
		 	        
		 	        while (sc2.hasNextLine()) 
		 	        {
		 	        	String temp8 = sc2.nextLine();
		 	        	outputString += (temp8); 
		 	        }
		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
		 	        ps.write(outputString.getBytes("UTF-8"));
		 	        sc2.close();
				}
		        if((request.compareTo("/registration") == 0)) {
		        	String r = "";
		        	String outputString = "";
		        	
		        	if(userData.isEmpty() || userData.size() != 2) {
						r = "You must enter both fields. Please try again.";
						outputString += "HTTP/1.1 200 OK\r\n";
						outputString += "Content-Type: text/plain\r\n";
						outputString += "Content-Length: " + r.length();
						outputString += "\r\n\r\n" + r;
					}
		        	else {
		        		if(authenticate.isUsernameValid(userData.get(0), "public/accountinfo.csv")) {
							if(authenticate.isPasswordValid(userData.get(1))) {
								byte[] salt = authenticate.getSalt();
//								byte[] token = authenticate.getToken("public/accountinfo.csv");
								String newPassword = authenticate.getSecurePassword(userData.get(1), salt);
								userData.set(1, newPassword);
								userData.add(Base64.getEncoder().encodeToString(salt));
//								userData.add(Base64.getEncoder().encodeToString(token));
								authenticate.toData("public/accountinfo.csv", userData);
								
								outputString += "HTTP/1.1 301 MOVED PERMANENTLY\r\n";
								outputString += "Content-Type: text/html\r\n";
								outputString += "Location: /index.html";
								outputString += "\r\n\r\n";
							}
							else {
								r = "";
								ArrayList<String> t = authenticate.getReqNotMet();
								for(int i = 0; i < t.size(); i++) {
									r = r + t.get(i) + "\r\n";
								}
								outputString += "HTTP/1.1 200 OK\r\n";
								outputString += "Content-Type: text/plain\r\n";
								outputString += "Content-Length: " + r.length();
								outputString += "\r\n\r\n" + r;
							}
						}
		        		else {
		        			r = "";
							ArrayList<String> t = authenticate.getReqNotMet();
							for(int i = 0; i < t.size(); i++) {
								r = r + t.get(i) + "\r\n";
							}
							outputString += "HTTP/1.1 200 OK\r\n";
							outputString += "Content-Type: text/plain\r\n";
							outputString += "Content-Length: " + r.length();
							outputString += "\r\n\r\n" + r;
						}
		        	}
		        	
		        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
		 	        ps.write(outputString.getBytes("UTF-8"));
				}
		        if((request.compareTo("/basic.css") == 0))
		        {
		        	File file = new File("public/basic.css");
		        	
	 	        	Scanner sc2 = new Scanner(file); 
	 	        	String outputString = "";
	 	        	
	 	        	while (sc2.hasNextLine()) 
	 	        	{
	 	        		String temp8 = sc2.nextLine();
	 	        		outputString += (temp8); 
	 	        	}
	 	        	
	 	        	test.printStreamGoodCSS(ps, outputString.getBytes("UTF-8").length);
	 	        	ps.write(outputString.getBytes("UTF-8"));
	 	        	sc2.close();
		        }
		        if((request.compareTo("/home.html") == 0))
		        {
		        	File file = new File("public/home.html"); 
	 	        	Scanner sc2 = new Scanner(file); 
	 	        	String outputString = "";
	 	        	
	 	        	while (sc2.hasNextLine()) 
	 	        	{
	 	        		String temp8 = sc2.nextLine();
	 	        		outputString += (temp8); 
	 	        	}
	 	        	
	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
	 	        	ps.write(outputString.getBytes("UTF-8"));
	 	        	sc2.close();
		        }
		        if((request.compareTo("/profile.html") == 0))
		        {
		        	File file = new File("public/profile.html"); 
		        	Scanner sc2 = new Scanner(file);
				 	String outputString = "";
					while (sc2.hasNextLine()) 
					{
						String temp8 = sc2.nextLine();
						if(temp8.contains("{{ personPosts }}"))
						{
							for(int i = 0; i < filename.size(); i++)
							{
								outputString += "<img src=\"" + filename.get(i) + "\"" + "    <li class=\"vote\">\r\n" + 
										"      <button class=\"upclick\" onclick=\"upvote(" +  i + ")\">up Vote</button>\r\n" + 
										"      <span id=\"currVotes" + i + "\" id=>" + votes.get(filename.get(i)) +"</span>\r\n" + 
										"      <button class=\"downclick\" onclick=\"downvote(" + i + ")\">down Vote</button>\r\n" + 
										"      <a>{{ title }}</a>\r\n" + 
										"    </li>";
							}
						}
						else
							outputString += (temp8) + "\r\n"; 
					}
		 	        
		 	        test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
		 	        ps.write(outputString.getBytes("UTF-8"));
		 	        sc2.close();
		        }
		        
		        /*
		         * For the multimedia makes public post
		         * 
		         * TODO add to document when post command is working 
		         */
		        if(request.compareTo("/addStatus") == 0){

		        /** TODO make sure data is added **/
		        	String idx = "publicFiles/info.txt";
					String fileData = readTextData(idx);
		        	String outputString = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n\r\n";
		        	outputString = "Content-Length: " + fileData.length() + "\r\n\r\n" + fileData;
					ps.write(outputString.getBytes("UTF-8"));
		        }
		        
		        /*
		         * To request all posts
		         */
		        if(request.compareTo("/statusUpdate") == 0) {
		        	String idx = "public/posts.txt";
					String fileData = readTextData(idx);
		        	String outputString = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n";
					outputString += "Content-Length: " + (fileData.length()) +"\r\n\r\n" + fileData;
					ps.write(outputString.getBytes("UTF-8"));
		        }
		        
		        for(int i = 0; i < filename.size(); i++)
		        {
		        	if(request.contains(filename.get(i)))
		        	{
			        	String tempstester[] = request.split("/");
						String actualrequest2 = tempstester[1];
						
			            File image = new File("Multimedia_Content/" + actualrequest2);
						if(!image.exists())
						{
							ps.write("Image file name does not exist".getBytes("UTF-8"));
						}
						else
						{
					        BufferedImage bufferimage = ImageIO.read(image);
				            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				            ImageIO.write(bufferimage, "png", byteArrayOutputStream);
		
				            int size = byteArrayOutputStream.toByteArray().length;
				            
				            test.printStreamGoodimg(ps, size);
				            
				            ps.write(byteArrayOutputStream.toByteArray());
				            
				            ps.flush();
						}
		        	}
		        }
		        if((request.compareTo("friends.html") == 0)) {
		        	File csvfile = new File("public/accountinfo.csv");
		        	Scanner scan = new Scanner(csvfile);
		        	String usernames = "These are our other users </br>";
		        	
		        	while (scan.hasNextLine()) {
		        		String accountinfo = scan.nextLine();
		        		String [] ainfo = accountinfo.split(",");
		        		usernames = usernames + ainfo[0] + "</br>";
		        	}
		        	
		        	File file = new File("public/friends.html"); 
	 	        	Scanner sc2 = new Scanner(file); 
	 	        	String outputString = "";
	 	        	
	 	        	while (sc2.hasNextLine()) 
	 	        	{
	 	        		String temp8 = sc2.nextLine();
	 	        		if (temp8.contains("{{ add friends }}")) {
	 	        			temp8.replace("{{ add friends }}", usernames);
	 	        		}
	 	        		outputString += (temp8); 
	 	        	}
	 	        	
	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
	 	        	ps.write(outputString.getBytes("UTF-8"));
	 	        	sc2.close();
		        }
		        if((request.compareTo("/dmtemplate.html") == 0))
		        {
		        	File file = new File("public/dmtemplate.html"); 
	 	        	Scanner sc2 = new Scanner(file); 
	 	        	String outputString = "";
	 	        	
	 	        	while (sc2.hasNextLine()) 
	 	        	{
	 	        		String temp8 = sc2.nextLine();
	 	        		outputString += (temp8); 
	 	        	}
	 	        	
	 	        	test.printStreamGoodHTML(ps, outputString.getBytes("UTF-8").length);
	 	        	ps.write(outputString.getBytes("UTF-8"));
	 	        	sc2.close();
		        }
		        if((request.compareTo("/script2.js") == 0))
		        {
		        	File file = new File("public/script2.js"); 
	 	        	Scanner sc2 = new Scanner(file); 
	 	        	String outputString = "";
	 	        	
	 	        	while (sc2.hasNextLine()) 
	 	        	{
	 	        		String temp8 = sc2.nextLine();
	 	        		outputString += (temp8); 
	 	        	}
	 	        	
	 	        	test.printStreamGoodJS(ps, outputString.getBytes("UTF-8").length);
	 	        	ps.write(outputString.getBytes("UTF-8"));
	 	        	sc2.close();
		        }
		        if((request.compareTo("/mountain.jpeg") == 0))
		        {
		        	String tempstest[] = request.split("/");
					String actualrequest = tempstest[1];
					
		            File image = new File("public/" + actualrequest);
					if(!image.exists())
					{
						ps.write("Image file name does not exist".getBytes("UTF-8"));
					}
					else
					{
				        BufferedImage bufferimage = ImageIO.read(image);
			            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			            ImageIO.write(bufferimage, "jpg", byteArrayOutputStream);
	
			            int size = byteArrayOutputStream.toByteArray().length;
			            
			            test.printStreamGoodimg(ps, size);
			            
			            ps.write(byteArrayOutputStream.toByteArray());
			            
			            ps.flush();
					}
		        }
		        if((request.compareTo("/socket") == 0))
		        {
		            clients.add(socket);
		            
		        	byte[] response = ("HTTP/1.1 101 Switching Protocol\r\n" + 
				        "Connection: Upgrade\r\n" + 
				        "Upgrade: websocket\r\n" + 
				        "Sec-WebSocket-Accept: " +
				        Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((websocketkey + GUID).getBytes("UTF-8"))) + 
				        "\r\n\r\n").getBytes("UTF-8");
		        	ps.write(response);
		        }
		        
		        for(int i = 0; i < clients.size(); i++)
		        {
		            Websocket temptest = new Websocket();
		            
		            temptest.socket(clients.get(i));
		        }
		        
		        ps.close();
		        breader.close();
		        inStream.close();
            }
        }
	}


	/**
	 * Reads the Request from the scanner and parses it. 
	 * It does not return due to the use of global variables in the WebServer Class.
	 */
	private static void readRequest() {
		//Reads the first line. Of the Get Request (ie. "GET /index HTTP/1.1")
		input = scaner.nextLine();
		System.out.println("\nThe client submitted:\n" + input); 
		if(input.contains("GET")) {
			makeGETResponse();
		}else if(input.contains("POST")) {
			makePOSTResponse();
		}else {
			System.out.println("\nThe System failed to detect a GET or POST request\n");
			response = new GetResponse("GET /404 HTTP/1.1");
		}
	}

	/**
	 * Parses the clients request so long it is a GET request
	 */
	private static void makeGETResponse() {
		response = new GetResponse(input);
		
		//Adds additionaly headers so long they exist (ie. "Host: localhost:8000" or "Cookie: user=visited")
		boolean headerExist = true; 
		while(headerExist) {
			String line = scaner.nextLine();
			System.out.println(line);
			
			//If the line exist we parse it in the response class. Other we break from the loop.
			if(line.isEmpty()) headerExist = false;
			else response.parseln(line);
		}
		
	}

	/**
	 * Parses the clients request so long it is a POST request
	 */
	private static void makePOSTResponse() {
		response = new PostResponse(input);
		
		//Adds additionally headers so long they exist (ie. "Host: localhost:8000" or "Cookie: user=visited")
		boolean headerExist = true; 
		while(headerExist) {
			String line = scaner.nextLine();
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
	 * From Alam HW5
	 * Reads the .txt Files and puts it into a string. Line by line
	 * @param Name of the files that needs to be converted as a String.
	 * @return Converted file as a string.
	 * @throws IOException
	 */
	private static String readTextData(String index) throws IOException {
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
	 * From Alam HW 5
	 * Method takes a string and adds it into a new document as a new line.
	 * @param String to be added to the document?
	 * @param The file that the string is being added to 
	 * @throws IOException 
	 */
	private static void addToDocument(String string, File txtFile) throws IOException {
		StatusUpdate post = new StatusUpdate(string);
		publicPosts.add(post);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtFile));
		for(StatusUpdate s : publicPosts) {
			String str = s.getCSV();
			bufferedWriter.write(str);
			bufferedWriter.newLine();
		}
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
		webList.put("/friends.html","public/friend.html");	
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
