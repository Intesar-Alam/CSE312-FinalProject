import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class ClientsInformation {
	private String type = "";
	private String request = "";
	private String version = "";
	private HashMap<String,String> Information  = new HashMap<String,String>();

	public void update(BufferedReader bufferedReader) throws IOException
	{
		String line = bufferedReader.readLine();
		System.out.println();
		System.out.println(line);
		String[] requests = line.split(" ");
		type = requests[0];
		request = requests[1]; 
		version = requests[2];
		
		while(bufferedReader.ready())
		{
			line = bufferedReader.readLine();
			if(line.length() == 0)
			{
				break;
			}
			else
			{
				String[] test = line.split(": ");
				Information.put(test[0], test[1]);
				System.out.println(line);
			}
		}
		
	}
	
	public HashMap<String,String> getHashMap()
	{
		return Information;
	}
	
	public String getRequest()
	{
		return request;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getVersion()
	{
		return version;
	}
}
