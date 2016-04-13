package client;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;


public class client{
	
	
	public static void main(String[] args){
		Scanner scanner;
		String url, host, subUrl, httpRequest;
		InetAddress hostAddress = null;
		OutputStream dos = null;
		DataInputStream dis = null;
		Socket socket = null;
		byte[] inblock = new byte[1024];
		while(true){
			scanner = new Scanner(System.in);
			System.out.println("please input your url, ctrl+c to exit..");
			url = scanner.nextLine();
			
			Pattern p = Pattern.compile("(http://)?(?<host>www.*?)(?<sbu>/.*?)?");
			Matcher mt = p.matcher(url);
			
			if(!mt.matches()){
				System.out.println("it is not a valid url");
				continue;
			}
			
			// generate http request
			System.out.println("tring to connect " + url + "..");
			host = mt.group("host");
			subUrl = mt.group("sbu"); 
			httpRequest = generateHttpRequest(host, subUrl);
			
			// reslove the host address
			try {
				hostAddress = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				System.out.println("name could not be resolved");
				e.printStackTrace();
			}
			
			// use socket to send http request
			try {
				System.out.println(hostAddress);
				socket = new Socket(hostAddress, 80);
				
				dos = socket.getOutputStream();
				dos.write(httpRequest.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// receive http respone
			try {
				System.out.println(inblock.toString());
				dis = new DataInputStream( new BufferedInputStream( socket.getInputStream() ));
				byte[] te = new byte[1];
				while(dis.read(te) != -1){
					System.out.println(Arrays.toString(te));
				}
				int num_byte_read = dis.read(inblock);
				System.out.println("read "+ num_byte_read +" bytes" + inblock.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	private static String generateHttpRequest(String hostAdd, String suburl){
		String ret;
		String requestColum = "GET "+suburl+" HTTP/1.1\r\n";
		String agent = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0\r\n";
		String host = "Host: "+hostAdd+"\r\n";
		ret = requestColum + agent + host + "\r\n";
		
		return ret;
	}
	public void keyListner(KeyEvent e){
		if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C)
			System.out.println("ok");
	}
	
	
}