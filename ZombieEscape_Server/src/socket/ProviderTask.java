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
			input = new BufferedReader(new InputStreamReader(clientSocket
					.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(clientSocket
					.getOutputStream()));

			newGamer(input.readLine());

			SocketMessage message;
			do {
				String line = input.readLine();
				// System.out.println(line);
				message = gson.fromJson(line, SocketMessage.class);

				System.out.println(gamer.getName() + "> " + message.command
						+ " - " + message.value);

				parseMessage(message);

			} while (!message.command.equals("bye"));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
				clientSocket.close();
				gamer.quitGame();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void newGamer(String message) {
		System.out.println("newGamer() entered");
		System.out.println("client>" + message);
		SocketMessage newGamer = gson.fromJson(message, SocketMessage.class);
		int gamerID;
		if (newGamer.command.equals("newGamer")) {
			gamer = new Gamer((String) newGamer.value, this);
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

		if ("newGame".equals(message.command)) {
			newGame(message.value);
		} else if ("listGames".equals(message.command)) {
			listGames();
		} else if ("addGamer".equals(message.command)) {
			addGamer(message.value);
		} else if ("removeGamer".equals(message.command)) {
			removeGamer();
		} else if ("setLocation".equals(message.command)) {
			setLocation(message.value);
		} else if ("bye".equals(message.command)) {
			// do nothing, is handled in run()
		} else {
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
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	private void addGamer(Object json) {
		Socket_AddGamer gamerInfo = gson.fromJson(json.toString(), Socket_AddGamer.class);
		gameManager.addGamerToGame(gamer, gamerInfo.gameID, gamerInfo.state);
		sendJSONObject(!gamer.isZombie());
	}

	private void listGames() {
		ArrayList<Game> currentGames = gameManager.getGamesClone();
		ArrayList<Socket_GameOverview> gameList = new ArrayList<Socket_GameOverview>(
				currentGames.size());
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
		GPS_location location = gson.fromJson(json.toString(),
				GPS_location.class);
		gamer.setLocation(location);

	}

	// commands send to the client

	public void fight() {
		sendJSONObject(new SocketMessage("fight"));
	}

	public void fightOver(boolean b) {
		sendJSONObject(new SocketMessage("fightOver", b));
	}

	public void listGamers(ArrayList<Socket_GamerOverview> overview) {
		System.out.println("send lsgamers to " + gamer.getName() );
		sendJSONObject(new SocketMessage("listGamers", overview));

	}

}
