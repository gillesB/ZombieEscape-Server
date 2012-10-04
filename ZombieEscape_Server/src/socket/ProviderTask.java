package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

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

	private final Semaphore gamerToAttackAvailable = new Semaphore(1, true);
	private Socket_AttackGamer gamerToAttack;

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

			SocketMessage message;
			do {
				String line;
				line = input.readLine();

				message = gson.fromJson(line, SocketMessage.class);

				parseMessage(message);
				// System.out.println("received line from " + gamer.getName() +
				// " " + line);
				System.out.println(Thread.currentThread().getName() + " received json from " + gamer.getName() + " " + message.command
				+ " - " + message.value);

			} while (!message.command.equals("bye"));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			gamer.quitGame();
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

	/**
	 * @param message
	 */
	private void parseMessage(SocketMessage message) {
		if ("newGamer".equals(message.command)) {
			newGamer(message.value.toString());
		} else if ("newGame".equals(message.command)) {
			newGame(message.value);
		} else if ("listGames".equals(message.command)) {
			listGames();
		} else if ("addGamer".equals(message.command)) {
			addGamer(message.value);
		} else if ("removeGamer".equals(message.command)) {
			removeGamer();
		} else if ("setLocation".equals(message.command)) {
			setLocation(message.value);
		} else if ("attack".equals(message.command)) {
			receiveGamerToAttack(message.value);
		} else if ("bye".equals(message.command)) {
			System.out.println("received a bye");
		} else {
			System.err.println("Unkown Command " + message.command + " from gamer " + gamer.getName());
		}
	}

	/**
	 * if gamer already exists, only change the gamername. Otherwise create a
	 * new one. In both cases send gamerID back to client.
	 * 
	 * @param gamername
	 */
	private void newGamer(String gamername) {
		int gamerID;
		if (gamer == null) {
			gamer = new Gamer(gamername, this);
			gamerID = gamer.getGamerID();
		} else {
			gamerID = gamer.getGamerID();
			gamer.setName(gamername);
		}
		System.out.println("new Gamer created with name: " + gamer.getName());
		sendJSONObject(gamerID);
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
		} catch (Exception e) {
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
		ArrayList<Socket_GameOverview> gameList = new ArrayList<Socket_GameOverview>(currentGames.size());
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

	// commands send to the client

	public void listGamers(ArrayList<Socket_GamerOverview> overview) {
		// System.out.println("send lsgamers to " + gamer.getName());
		sendJSONObject(new SocketMessage("listGamers", overview));

	}

	/**
	 * Benachrichtigt den Spieler dass er sich nun in einem Kampf befindet.
	 */
	public void fight() {
		sendJSONObject(new SocketMessage("fight"));
	}

	/**
	 * Sendet dem Spieler die Liste mit den Spielern, die sich bei ihm im Kampf
	 * befinden. Das Mutex <Code>gamerToAttackAvailable</Code> wird geschlossen,
	 * so dass später auf den Client gewartet werden kann. Bis dieser sich
	 * entschieden hat, welchen Gegner er angreift.
	 * 
	 * @param fightingGamers
	 *            Die Liste mit den Spielern die sich im Kampf befinden.
	 */
	public void listFightingGamers(ArrayList<Socket_GamerInFight> fightingGamers) {
		System.out.println("send lsOpponents to " + gamer.getName() + " " + Thread.currentThread().getName());
		sendJSONObject(new SocketMessage("listFightingGamers", fightingGamers));

		try {
			gamerToAttackAvailable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Erhält ein Json-Objekt das einem Äquivalent zu einem
	 * <Code>Socket_AttackGamer</Code>-Objekt darstellt. Dieses Objekt wird
	 * deserialisiert und in der Objektvariablen <Code>gamerToAttack</Code>
	 * abgespeichert. Das Mutex <Code>gamerToAttackAvailable</Code> wird
	 * geöffnet und signalisiert damit, dass der anzugreifende Spieler gesetzt
	 * wurde.
	 * 
	 * @param json
	 *            Ein Json-Objekt mit dem Aufbau der Klasse
	 *            <Code>Socket_AttackGamer</Code>
	 */
	public void receiveGamerToAttack(Object json) {
		gamerToAttack = gson.fromJson(json.toString(), Socket_AttackGamer.class);
		gamerToAttackAvailable.release();
	}

	/**
	 * Wartet bis der anzugreifende Spieler gesetzt ist und gibt diesen dann
	 * zurück, sowie der Wert wieviel Schaden dass dieser nimmt.
	 * <p>
	 * Schließt den Mutex <Code>gamerToAttackAvailable</Code>, falls diese noch
	 * geschlossen ist, muss gewartet werden, bis dieser geöffnet ist. Damit
	 * wird sicher gegangen dass die Objektvariablen <Code>gamerToAttack</Code>
	 * mit dem aktullen Wert belegt wurde. Die Werte der Objektvariablen werden
	 * in eine lokale Variable copiert, die zu Schluss zurückgegeben wird. Das
	 * Mutex wird wieder geöffnet, so dass die Objektvariable neu beschrieben
	 * werden kann.
	 * </p>
	 * 
	 * @return der anzugreifende Spieler und der Schadenswert
	 */
	public Socket_AttackGamer getGamerToAttack() {
		Socket_AttackGamer gamer_clone = null;
		try {
			gamerToAttackAvailable.acquire();
			gamer_clone = gamerToAttack.copy();
			gamerToAttackAvailable.release();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return gamer_clone;
	}

	/**
	 * Benachrichtigt den Spieler dass der Kampf beendet wurde und gibt ihm an,
	 * ob er überlebt hat oder nicht.
	 * 
	 * @param aliveOrDeath true = überlebt, false = tot
	 */
	public void fightOver(boolean aliveOrDeath) {
		sendJSONObject(new SocketMessage("fightOver", aliveOrDeath));
	}

}
