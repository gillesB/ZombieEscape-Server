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
		// create a new gamer on the server, this step has to be done everytime,
		// as the gamers are not saved
		this.sendJSONObject(new SocketMessage("newGamer", gamerName));

		// get the gamerID till now it is not needed, but could be usefull in
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

	Socket_GameOverview[] getGameList() throws IOException {
		sendJSONObject(new SocketMessage("listGames"));
		String gamelist = socketIn.readLine();
		Socket_GameOverview[] gameArray = gson.fromJson(gamelist, Socket_GameOverview[].class);
		return gameArray;
	}

	boolean joinGame(int gameID) throws JsonSyntaxException, IOException {
		sendJSONObject(new SocketMessage("addGamer", ((Integer) gameID).toString()));
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
		sendJSONObject(new SocketMessage("setLocation", myLocation));
	}

	boolean joinGameBotnet() throws IOException {
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
		return joinGame(botnetGameID);
	}

	SocketMessage getMessageFromServer() throws IOException {
		String gamelist = socketIn.readLine();
		return gson.fromJson(gamelist, SocketMessage.class);
	}

	double distanceTo(GPS_location locationOfGamer) {
		return haversine_km(myLocation.latitude, myLocation.longitude, locationOfGamer.latitude,
		locationOfGamer.longitude);
	}

	private double haversine_km(double lat1, double long1, double lat2, double long2) {
		double toRad = 0.0174532925199433; // pi / 180
		double dlong = (long2 - long1) * toRad;
		double dlat = (lat2 - lat1) * toRad;
		double a =
		Math.pow(Math.sin(dlat / 2.0), 2) + Math.cos(lat1 * toRad) * Math.cos(lat2 * toRad)
		* Math.pow(Math.sin(dlong / 2.0), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double d = 6367 * c;

		return d;
	}

	GPS_location goInDirection(GPS_location target, double stepSize) {
		
		if(target == null){
			return myLocation;
		}
		
		if(target.equals(myLocation)){
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
