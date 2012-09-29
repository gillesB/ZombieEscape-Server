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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import server.GPS_location;
import socket.SocketMessage;
import socket.Socket_GameOverview;
import socket.Socket_AddGamer;

public abstract class AutoNetworkConnection {

	int portNumber = 2004;
	Socket socket;
	BufferedWriter socketOut;
	BufferedReader socketIn;
	GPS_location myLocation;

	static Gson gson = new Gson();

	boolean openConnectionSuccess;
	private Boolean loggedIn;

	void openConnection(final String IPaddress) throws InterruptedException {
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

	int newGamer(String gamerName) throws JsonSyntaxException, IOException {
		// create a new gamer on the server, this step has to be done every time,
		// as the gamers are not saved
		this.sendJSONObject(new SocketMessage("newGamer", gamerName));

		// get the gamerID till now it is not needed, but could be useful in
		// the future
		int gamerID = gson.fromJson(socketIn.readLine(), Integer.class);
		System.out.println(gamerID);
		return gamerID;
	}

	int newGame(String gamename) throws JsonSyntaxException, IOException {
		sendJSONObject(new SocketMessage("newGame", gamename));
		int gameID = gson.fromJson(socketIn.readLine(), Integer.class);
		return gameID;
	}

	void closeConnection() {
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

	/**
	 * @param msg
	 */
	void sendJSONObject(Object obj) {
		try {
			String json = gson.toJson(obj);
			socketOut.write(json + "\n");
			socketOut.flush();
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

	Socket_GameOverview[] getGameList() throws IOException {
		sendJSONObject(new SocketMessage("listGames"));
		String gamelist = socketIn.readLine();
		Socket_GameOverview[] gameArray = gson.fromJson(gamelist, Socket_GameOverview[].class);
		return gameArray;
	}

	boolean joinGame(int gameID, int state) throws JsonSyntaxException, IOException {
		Socket_AddGamer socketAddGamer = new Socket_AddGamer(((Integer) gameID).toString(),state); 
		sendJSONObject(new SocketMessage("addGamer", socketAddGamer));
		boolean human = gson.fromJson(socketIn.readLine(), Boolean.class);
		printJoinedGameMessage(human);
		return human;
	}

	void printJoinedGameMessage(boolean human) {
		if (human) {
			System.out.println("I am a human.");
		} else {
			System.out.println("BRAAAIIIINNNNZZZZZ!!!!");
		}
	}

	void setLocation(GPS_location loc) {
		setLocation(loc.longitude, loc.latitude);
	}

	void setLocation(double longitude, double latitude) {
		myLocation = new GPS_location(latitude, longitude);
		//System.out.println("new location set: " + myLocation);
		sendJSONObject(new SocketMessage("setLocation", myLocation));
	}

	boolean joinGameBotnet(int state) throws IOException {
		Socket_GameOverview[] games = getGameList();
		boolean gameBotnetExists = false;
		int botnetGameID = -1;
		for (Socket_GameOverview g : games) {
			if (g.name.equals("Botnet")) {
				botnetGameID = g.gameID;
				gameBotnetExists = true;
			}
		}
		if (!gameBotnetExists) {
			botnetGameID = this.newGame("Botnet");
		}
		return joinGame(botnetGameID,state);
	}

	SocketMessage getMessageFromServer() throws IOException {
		String gamelist = socketIn.readLine();
		return gson.fromJson(gamelist, SocketMessage.class);
	}

	GPS_location goInDirection(GPS_location target, double stepSize) {

		if (target == null) {
			return myLocation;
		}

		if (target.equals(myLocation)) {
			return myLocation;
		}

		double direction = myLocation.longitude < target.longitude ? 1 : -1;

		// equation of a line (Geradengleichung)
		double numerator1 = target.latitude - myLocation.latitude;
		double numerator2 = target.longitude * myLocation.latitude - myLocation.longitude * target.latitude;
		// TODO avoid division by zero
		double denominator = target.longitude - myLocation.longitude;

		double x = myLocation.longitude + (stepSize * direction);
		double y = numerator1 / denominator * x + numerator2 / denominator;
		return new GPS_location(y, x);

	}

}
