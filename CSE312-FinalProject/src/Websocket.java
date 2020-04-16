import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;

public class Websocket {

	private int payloadlength;
	private ArrayList<String> messages = new ArrayList<String>(); 
	Socket socket;
	
	public void socket(Socket s) throws IOException
	{
		socket = s;
		InputStream input = socket.getInputStream();
		while(socket.isConnected())
        {
			System.out.println(input.available());
			
            int numbyte = input.read();

            byte[] readinput = new byte[input.available() + 1];
            readinput[0] = (byte) numbyte;
            byte[] lengthamt;
            
        	int nextbytevalue;
        	System.out.println((Byte.toUnsignedInt(readinput[1]) - 128));
        	if((Byte.toUnsignedInt(readinput[1]) - 128) == 127)
    		{
        		int length1 = Byte.toUnsignedInt(readinput[2]);
        		int length2 = Byte.toUnsignedInt(readinput[3]);
        		int length3 = Byte.toUnsignedInt(readinput[4]);
        		int length4 = Byte.toUnsignedInt(readinput[5]);
        		int length5 = Byte.toUnsignedInt(readinput[6]);
        		int length6 = Byte.toUnsignedInt(readinput[7]);
        		int length7 = Byte.toUnsignedInt(readinput[8]);
        		int length8 = Byte.toUnsignedInt(readinput[9]);
        		
        		lengthamt = new byte[8];
        		lengthamt[0] = readinput[2];
        		lengthamt[1] = readinput[3];
        		lengthamt[2] = readinput[4];
        		lengthamt[3] = readinput[5];
        		lengthamt[4] = readinput[6];
        		lengthamt[5] = readinput[7];
        		lengthamt[6] = readinput[8];
        		lengthamt[7] = readinput[9];
        		
        		String stringlength = Integer.toBinaryString(length1) + Integer.toBinaryString(length2) + Integer.toBinaryString(length3) + 
        				Integer.toBinaryString(length4) + Integer.toBinaryString(length5) + Integer.toBinaryString(length6) + 
        				Integer.toBinaryString(length7) + Integer.toBinaryString(length8);
        		
        		payloadlength = Integer.parseInt(stringlength, 2);
        		
        		
        		
        		nextbytevalue = 10;
    		}
        	else if((Byte.toUnsignedInt(readinput[1]) - 128) == 126)
        	{
        		int length1 = Byte.toUnsignedInt(readinput[2]);
        		int length2 = Byte.toUnsignedInt(readinput[3]);
        		
        		payloadlength = length1 << 8 | length2;
        		
        		lengthamt = new byte[8];
        		lengthamt[0] = readinput[2];
        		lengthamt[1] = readinput[3];
        		
        		nextbytevalue = 4;
        	}
        	else
        	{
        		payloadlength = (Byte.toUnsignedInt(readinput[1]) - 128);
        		nextbytevalue = 2;
        	}
        	
        	System.out.println(payloadlength);
        	int maskingbit = 0;
        	
        	if(Byte.toUnsignedInt(readinput[1]) > 127)
        	{
        		maskingbit = 1;
        	}

    		byte[] maskingkey = new byte[4];
        	if(maskingbit == 1)
        	{
        		maskingkey[0] = readinput[nextbytevalue]; 
        		nextbytevalue++;
        		maskingkey[1] = readinput[nextbytevalue];
        		nextbytevalue++;
        		maskingkey[2] = readinput[nextbytevalue];
        		nextbytevalue++;
        		maskingkey[3] = readinput[nextbytevalue];
        		nextbytevalue++;
        	}
        	
        	byte[] decoded = new byte[payloadlength];
			byte[] encoded = new byte[payloadlength];
			for(int i = 0; i < payloadlength; i++)
			{
				encoded[i] = readinput[i + nextbytevalue];
			}
			for (int i = 0; i < encoded.length; i++) {
				decoded[i] = (byte) (encoded[i] ^ maskingkey[i & 0x3]);
			}
			
			String message = new String(decoded);
        	
			messages.add(message);
			
			sendinfo();
        }
	}
	
	public ArrayList<String> getMessages()
	{
		return messages;
	}
	
	public void sendinfo() throws IOException
	{
		
		PrintStream ps = new PrintStream(socket.getOutputStream());
		
		for(int i = 0; i < messages.size(); i++)
		{
			int retpllength = messages.get(i).length();
			byte[] firstbyte = {(byte) 129};
			byte[] length;
			if(retpllength > 65535)
			{
				byte[] holdbyte = {(byte) 127};
			    BigInteger bigInt = BigInteger.valueOf(retpllength);
			    length = bigInt.toByteArray();
			    
			    length = concat(holdbyte, length);
			}
			else if(retpllength > 125)
			{
				byte[] holdbyte = {(byte) 126};
			    BigInteger bigInt = BigInteger.valueOf(retpllength);
			    length = bigInt.toByteArray();
			    
			    length = concat(holdbyte, length);
			}
			else
			{
			    BigInteger bigInt = BigInteger.valueOf(retpllength);
			    length = bigInt.toByteArray();
			}
			
			byte[] content = messages.get(i).getBytes();
			
			byte[] val = concat(firstbyte, length);
			val = concat(val, content);
			
			ps.write(val);
		}
	}
	
	public byte[] concat(byte[] a, byte[] b)
	{
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
}
