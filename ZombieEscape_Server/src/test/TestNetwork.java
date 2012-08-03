package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import socket.SocketMessage;

import com.google.gson.Gson;

/**
 * A singleton class, which is accessible through the whole client. It is the
 * only class which has a direct connection to the server. It implements the
 * socket commands, described in the document of Rosebud
 * 
 * 
 */
public class TestNetwork {

	private int portNumber = 2004;
	private Socket socket;
	private static BufferedWriter socketOut;
	private static BufferedReader socketIn;
	private static final TestNetwork instance = new TestNetwork();
	static Gson gson = new Gson();

	private String username;
	private Boolean loggedIn;

	// Private constructor prevents instantiation from other classes
	private TestNetwork() {
		System.out.println("NetworkSingleton started");
	}

	public static TestNetwork getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		TestNetwork tn = TestNetwork.getInstance();
		try {
			tn.openConnection("127.0.0.1");
			sendJSONObject(new SocketMessage("newGamer","master xardas"));
			int gamerID = gson.fromJson(socketIn.readLine(), Integer.class);
			System.out.println(gamerID);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * @param msg
	 */
	private static void sendJSONObject(Object obj) {
		try {
			String json = gson.toJson(obj);
			socketOut.write(json+"\n");
			socketOut.flush();
			System.out.println("client>" + json);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	/**
	 * logs out. the connection to the server is closed on every log out
	 */
	public void logout() {
		loggedIn = false;
		if (socket != null) {
			closeConnection();
		}
	}

	private boolean openConnectionSuccess;

	private void openConnection(final String IPaddress) throws InterruptedException {
		Thread t = new Thread() {

			@Override
			public void run() {
				try {
					InetAddress serverAddr = InetAddress.getByName(IPaddress);

					System.out.println("Create Socket");

					socket = new Socket();
					socket.connect(new InetSocketAddress(serverAddr, portNumber));

					socketOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					openConnectionSuccess = true;
				} catch (UnknownHostException e) {
					System.out.println(e);
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		};
		openConnectionSuccess = false;
		t.start();
		t.join();
	}

	private void closeConnection() {
		Thread t = new Thread() {
			public void run() {
				try {
					socketOut.write("bye");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						socketOut.close();
						socketIn.close();
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		t.start();
	}

}
