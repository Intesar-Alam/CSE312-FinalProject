import java.awt.image.BufferedImage;
import java.io.BufferedReader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class Webserver {
	private static ServerSocket server;
	private static int port = 8000;
	
	public static void main(String args[]) throws IOException, NoSuchAlgorithmException {
		server = new ServerSocket(port);
        System.out.println("Running Server now on port: " + port);
        List<Socket> clients = new ArrayList<>();
        
        while(true){
            try(Socket socket = server.accept())
            {
	            InputStream input = socket.getInputStream();
	            
	            BufferedReader sc = new BufferedReader(new InputStreamReader(input));
		        
		        ClientsInformation temp = new ClientsInformation();
		        temp.update(sc);
		        String request = temp.getRequest();
		        
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
		        if((request.compareTo("/favicon.ico") == 0))
		        {
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
		        if((request.compareTo("/Signup.html") == 0))
		        {
		        	File file = new File("public/signup.html"); 
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
		        sc.close();
		        input.close();
            }
        }
	}

	
}
