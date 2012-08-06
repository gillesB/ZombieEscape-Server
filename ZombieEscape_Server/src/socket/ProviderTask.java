package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import server.GPS_location;
import server.Game;
import server.GameManager;
import server.Gamer;

import com.google.gson.Gson;

/**
 * makes the actual communication with a client. See Socket Commands in the
 * documentation for more detail.
 * 
 */
public class ProviderTask implements Runnable {
	protected Socket clientSocket = null;
	BufferedReader input;
	BufferedWriter output;
	Gson gson = new Gson();
	GameManager gameManager;

	Gamer gamer;

	// RecommenderSystem recommender;

	public ProviderTask(Socket clientSocket, GameManager gameManager) {
		this.clientSocket = clientSocket;
		this.gameManager = gameManager;
	}

	@Override
	public void run() {
		try {
			// 3. get Input and Output streams
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			newGamer(input.readLine());

			try {
				SocketMessage message;
				do {

					message = gson.fromJson(input.readLine(), SocketMessage.class);

					System.out.println("client>" + message.command);

					parseMessage(message);

				} while (!message.command.equals("bye"));
			} catch (EOFException e) {// occures when client stalls
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void newGamer(String message) {
		System.out.println("newGamer() entered");
		SocketMessage newGamer = gson.fromJson(message, SocketMessage.class);
		int gamerID;
		if (newGamer.command.equals("newGamer")) {
			gamer = new Gamer((String) newGamer.value);
			gamerID = gamer.getGamerID();
		} else {
			gamerID = -1;
		}
		sendJSONObject(gamerID);
	}

	/**
	 * @param message
	 */
	private void parseMessage(SocketMessage message) {
		switch (message.command) {
		case ("newGame"):
			newGame(message.value);
			break;
		case ("listGames"):
			listGames();
			break;
		case ("addGamer"):
			addGamer(message.value);
			break;
		case ("removeGamer"):
			removeGamer();
			break;
		case ("setLocation"):
			setLocation(message.value);
			break;
		case ("bye"):
			// do nothing, is handled in run()
			break;
		default:
			System.err.println("Unkown Command: " + message.command);
		}
	}

	/**
	 * @param msg
	 */
	private void sendJSONObject(Object obj) {
		try {
			String json = gson.toJson(obj);
			output.write(json + "\n");
			output.flush();
			System.out.println("server>" + json);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void addGamer(Object json) {
		String gameID = (String) json;
		gameManager.addGamerToGame(gamer, gameID);
		sendJSONObject(true);
	}

	private void listGames() {
		ArrayList<Game> currentGames = gameManager.getGamesClone();
		ArrayList<Socket_GameOverview> gameList = new ArrayList<>(currentGames.size());
		for (Game g : currentGames) {
			Socket_GameOverview go = new Socket_GameOverview();
			go.amountGamers = g.getActiveGamersCount();
			go.gameID = g.getGameID();
			go.name = g.getName();
			GPS_location gps = g.getLocation();
			go.longitude = gps.longitude;
			go.latitude = gps.latitude;
			gameList.add(go);
		}
		sendJSONObject(gameList);
	}

	private void newGame(Object json) {
		String gamename = (String) json;
		int gameID = gameManager.createGame(gamename);
		sendJSONObject(gameID);
	}

	private void removeGamer() {
		gamer.quitGame();
		sendJSONObject(true);
	}

	private void setLocation(Object json) {
		GPS_location location = gson.fromJson(json.toString(), GPS_location.class);
		gamer.setLocation(location);

	}

	
	//commands send to the client	
	
	public void fight(){
		sendJSONObject(new SocketMessage("fight"));
	}
	
	public void fightOver(boolean b) {
		sendJSONObject(new SocketMessage("fightOver", b));		
	}
	
	public void listGamers(ArrayList<Socket_GamerOverview> overview){
		sendJSONObject(new SocketMessage("listGamers", overview));
		
	}

}
