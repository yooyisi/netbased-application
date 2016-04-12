package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Server{
	private static String IP = "localhost";
	private static int port = 3000;
	
	
	public static void main(String[] args){
		listen_control_info();
	}
	
	private static void listen_control_info() {
		DatagramSocket udp_socket = null;
		
		try {
			udp_socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		byte block[] = new byte[256];
		DatagramPacket inpacket = new DatagramPacket(block,block.length);
		udp_socket.receive(inpacket);
		int length = inpacket.getLength();
		byte inblock[] = inpacket.getData();
		String info = new String(inblock,0,length);
	}
	
	private void send_file(){
		
	}
}