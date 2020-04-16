import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class OutPrintStream {
	
	public void printStreamGoodPlain(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: text/plain\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printStreamGoodHTML(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printStreamGoodHTMLandSetCookie(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        ps.write("Set-Cookie: SessionID=visited\r\n\r\n".getBytes("UTF-8"));
	}
	
	public void printStreamGoodCSS(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: text/css\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printStreamGoodJS(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: text/javascript;charset=UTF-8\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printStreamGoodimg(PrintStream ps, int length) throws IOException
	{
        ps.write("HTTP/1.1 200 OK\r\n".getBytes("UTF-8"));
        ps.write("Content-Type: image/png\r\n".getBytes("UTF-8"));
        String outputLength = "Content-Length: " + length + "\r\n\r\n";
        try {
			ps.write(outputLength.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void printStreamMoved(PrintStream ps, String location) throws IOException
	{
		ps.write("HTTP/1.1 301 Moved Permanently\r\n".getBytes("UTF-8"));
        ps.write(("Location: /" + location + "\r\n\r\n").getBytes("UTF-8"));
	}
}
