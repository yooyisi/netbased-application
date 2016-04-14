package client;

import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
		String url, host, subUrl, httpRequest, httpResponse = null;
		StringBuilder str_builder = new StringBuilder();
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
				socket = new Socket(hostAddress, 80);
				
				dos = socket.getOutputStream();
				dos.write(httpRequest.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// receive http respone, store it in httpResponse
			try {
				dis = new DataInputStream( new BufferedInputStream( socket.getInputStream() ));
				byte[] te = new byte[1];
				while(dis.read(te) != -1){
					String str = new String(te);
					str_builder.append(str);
					//System.out.print(str);
				}
				httpResponse = str_builder.toString();
				//System.out.println(httpResponse);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// analysis the http response
			processHttpResponse(httpResponse);
		}
	}
	
	
	private static String generateHttpRequest(String hostAdd, String suburl){
		String ret;
		String requestColum = "GET "+suburl+" HTTP/1.0\r\n";
		String agent = "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:45.0) Gecko/20100101 Firefox/45.0\r\n";
		String host = "Host: "+hostAdd+"\r\n";
		ret = requestColum + agent + host + "\r\n";
		
		return ret;
	}
	
	private static void processHttpResponse(String response) {/*
		Pattern p = Pattern.compile("HTTP/1.(0|1).*?(?<code>\\d+ \\w+)(.*?\\r?\\n?)*"
				+ "Content-Type: (?<type>.*?)/(.*?\\r?\\n?)*(?<body>\\<(?:.*?\\r\\n)*)", Pattern.DOTALL);  */
		Pattern p = Pattern.compile("HTTP/1.(0|1).*?(?<code>\\d+ \\w+)(.*?)"
				+ "Content-Type: (?<type>.*?)/(.*?)(?<body>\\<[\\s\\S]*)", Pattern.DOTALL);
		Matcher m = p.matcher(response);
		if(m.find()){
			String code = m.group("code");
			String type = m.group("type");
			String body = m.group("body");
			System.out.println("############################");
			System.out.println("code is "+code);
			System.out.println("############################");
			System.out.println("type is "+type);
			System.out.println("############################");
			System.out.println("body is "+body);
			saveToFile(type, body);
			
		}else{
			System.out.println("content not found!");
		}
	}
	
	private static void saveToFile(String type, String body){
		byte[] file_info = body.getBytes();
		//System.out.println(Arrays.toString(file_info));
		String postfix = null;
		if(type=="text")	postfix = "html";
		if(type=="image")	postfix = "jpg";
		try {
			OutputStream f = new FileOutputStream(System.getProperty("user.dir")+"/src/client/download."+postfix);
			f.write(file_info);
			System.out.println("file was already successfully saved.");
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("file not found");
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
	}
	
	public void keyListner(KeyEvent e){
		if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C)
			System.out.println("ok");
	}
	
	
}