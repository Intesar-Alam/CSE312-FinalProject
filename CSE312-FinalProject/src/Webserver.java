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
            	System.out.println("New client connected");
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
		        
		        if(info.containsKey("Sec-WebSocket-Key"))
		        {
		        	websocketkey = info.get("Sec-WebSocket-Key");
		        }
		        
		        if((request.compareTo("/") == 0) || (request.compareTo("/index.html") == 0))
			    {
					File file = new File("CSE312-FinalProject/public/index.html"); 
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
		        if((request.compareTo("/basic.css") == 0))
		        {
		        	File file = new File("CSE312-FinalProject/public/basic.css"); 
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
		        	File file = new File("CSE312-FinalProject/public/home.html"); 
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
		        	File file = new File("CSE312-FinalProject/public/profile.html"); 
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
		        if((request.compareTo("/Signup.html") == 0))
		        {
		        	File file = new File("CSE312-FinalProject/public/signup.html"); 
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
		        	File file = new File("CSE312-FinalProject/public/dmtemplate.html"); 
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
		        if((request.compareTo("/script.js") == 0))
		        {
		        	File file = new File("script.js"); 
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
					System.out.println(actualrequest);
					
		            File image = new File("CSE312-FinalProject/public/" + actualrequest);
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
