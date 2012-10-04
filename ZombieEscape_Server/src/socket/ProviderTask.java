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
 * Stellt die eigentliche Verbindung mit dem Client dar und läuft in einem
 * eigenen Thread. Besitzt eine 1 zu 1 Verbindung zu einem <code>Gamer</code>
 * -Objekt. Somit kann sichergegangen werden dass immer der richtige Client die
 * Nachrichten erhält. Außerdem konnte somit die Netzwerklogik, größtenteils von
 * der Spiellogik getrennt werden.</br>Vereinfacht dargestellt diese Klasse wird
 * nur benötigt um Informationen zwischen einem Client und einem
 * <code>Gamer</code> -Objekt zu tauschen. </br> Einzelheiten zu den Befehlen
 * die verschickt werden, finden sich in der Dokumentation des Projektes.
 * 
 * 
 */
public class ProviderTask implements Runnable {
	protected Socket clientSocket = null;
	BufferedReader input;
	BufferedWriter output;
	/**
	 * Der Json Serialisierer/Deserialisierer von Google für Java. Mit ihm
	 * lassen sich Java Objekte einfach in Json Objekte (Strings mit einem
	 * bestimmten Format) serialisieren und umgedreht.
	 */
	Gson gson = new Gson();
	GameManager gameManager;
	Gamer gamer;

	/**
	 * Die <code>Semaphore gamerToAttackAvailable</code> wird als Mutex benutzt.
	 * D.h. sie kann nur jeweils einmal geschlossen bzw. geöffnet werden. Mit
	 * ihr wird sichergegangen, dass die Variable <code>gamerToAttack</code> neu
	 * gesetzt wurde, nachdem der Client einen Gegner zum angreifen auswählen
	 * soll. Ist dies noch nicht passiert, so wird an der entsprechenden Stelle
	 * solange gewartet, bis der anzugreifende Gegner ausgewählt wurde, also die
	 * Variable <code>gamerToAttack</code> gesetzt wurde.
	 */
	private final Semaphore gamerToAttackAvailable = new Semaphore(1, true);
	private Socket_AttackGamer gamerToAttack;

	// RecommenderSystem recommender;

	public ProviderTask(Socket clientSocket, GameManager gameManager) {
		this.clientSocket = clientSocket;
		this.gameManager = gameManager;
	}

	/**
	 * Die Schleife des <code>ProviderTask</code>. Sie wartet auf eine Nachricht
	 * des Clients und verwertet diese. Die Verbindung bleibt solange bestehen,
	 * bis das Kommando bye geschickt wird.
	 */
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
	 * Parst den Befehl der vom Client erhalten wurde und führt die
	 * entsprechende Funktion aus.
	 * 
	 * @param message
	 *            der Befehl/Nachricht vom CLient
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
	 * Erstellt ein neues <code>Gamer</code>-Objekt mit dem Namen
	 * <code>gamername</code>, das in 1 zu 1 Verbindung mit diesem
	 * <code>ProviderTask</code> steht. </br> Existiert bereits ein Spieler für
	 * diesen <code>ProviderTask</code>, so wird nur der Spielername angepasst.
	 * In beiden Fällen wird die ID des Spielers an den Client gesendet.
	 * 
	 * @param gamername
	 *            Neuer Spielername
	 */
	private void newGamer(String gamername) {
		if (gamer == null) {
			gamer = new Gamer(gamername, this);
			System.out.println("new Gamer created with name: " + gamer.getName());
		} else {
			gamer.setName(gamername);
			System.out.println("changed gamername to: " + gamer.getName());
		}
		sendJSONObject(gamer.getGamerID());
	}

	/**
	 * Erhält ein Objekt <code>obj</code>, serialisiert es nach Json und sendet
	 * es dem Client.
	 * 
	 * <p>
	 * Das, als Json, serialisierte Objekt wird in den OutputStream geschrieben,
	 * dabei wird ein newline-character angehängt. Somit kann der Client ein
	 * Objekt per Zeile auslesen. Clientseitig wird demnach ein Äquivalent zu
	 * der Methode java.io.BufferedReader.readLine() benötigt. Weiterhin wird
	 * dort ebenfalls eine Möglichkeit benötigt um Json zu serialisieren bzw.
	 * deserialisieren.
	 * </p>
	 * 
	 * 
	 */
	private void sendJSONObject(Object obj) {
		try {
			String json = gson.toJson(obj);
			output.write(json + "\n");
			// make sure to send the object
			output.flush();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Kommando addGamer vom Client erhalten. Benutze die Informationen aus der
	 * Nachricht um den Spieler zu eindem Spiel hinzuzufügen. Antworte mit
	 * <code>true</code> falls der Spieler ein Mensch ist, ist er ein Zombie
	 * antworte mit <code>false</code>.
	 * 
	 * @param json
	 *            Informationen um den Spieler zu einem Spiel hinzuzufügen
	 */
	private void addGamer(Object json) {
		Socket_AddGamer gamerInfo = gson.fromJson(json.toString(), Socket_AddGamer.class);
		gameManager.addGamerToGame(gamer, gamerInfo.gameID, gamerInfo.state);
		sendJSONObject(!gamer.isZombie());
	}

	/**
	 * Kommando listGames vom Client erhalten. Sende dem Client eine Liste mit
	 * den Spielen, die der <code>gamemanager</code> verwaltet.
	 */
	private void listGames() {
		ArrayList<Game> currentGames = gameManager.getGamesClone();
		ArrayList<Socket_GameOverview> gameList = new ArrayList<Socket_GameOverview>(currentGames.size());
		for (Game g : currentGames) {
			Socket_GameOverview go = new Socket_GameOverview();
			go.amountGamers = g.getActiveGamersCount();
			go.gameID = g.getGameID();
			go.name = g.getName();
			GPS_location gps = g.getAverageLocation();
			go.longitude = gps.longitude;
			go.latitude = gps.latitude;
			gameList.add(go);
		}
		sendJSONObject(gameList);
	}

	/**
	 * Kommando newGame vom Client erhalten. Erstelle ein neues Spiel mit dem
	 * mitgesendeten Namen. Antworte dem Client mit der ID des neuen Spiels.
	 * 
	 * @param json
	 *            der mitgesendete Spielname
	 */
	private void newGame(Object json) {
		String gamename = (String) json;
		int gameID = gameManager.createGame(gamename);
		sendJSONObject(gameID);
	}

	/**
	 * Kommando removeGamer vom Client erhalten. Entfernt den Spieler aus seinem
	 * Spiel. Antwortet mit <code>true</code> falls dies erfolgreich war,
	 * andernfalls <code>false</code>.
	 */
	private void removeGamer() {
		sendJSONObject(gamer.quitGame());
	}

	/**
	 * Kommando removeGamer vom Client erhalten. Deserialisiert den Eingaberwert
	 * <code>json</code> zu einem <code>GPS_location</code>-Objekt. Die Position
	 * des Spielers wird entsprechend angepasst.
	 * 
	 * @param json
	 *            die mitgesendeten Koordinaten des Spielers
	 */
	private void setLocation(Object json) {
		GPS_location location = gson.fromJson(json.toString(), GPS_location.class);
		gamer.setLocation(location);

	}

	// commands send to the client
	/**
	 * Schickt eine Liste mit den Spieler die im gleichen Spiel sind wie
	 * <code>gamer</code> an den Client.
	 * 
	 * @param overview
	 *            die Liste mit den Spielern die gesendet werden soll
	 */
	public void listGamers(ArrayList<Socket_GamerOverview> overview) {
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
	 * @param aliveOrDeath
	 *            true = überlebt, false = tot
	 */
	public void fightOver(boolean aliveOrDeath) {
		sendJSONObject(new SocketMessage("fightOver", aliveOrDeath));
	}

}
