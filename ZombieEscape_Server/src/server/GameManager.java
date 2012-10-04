package server;

import java.util.ArrayList;

import socket.ParallelProvider;

/**
 * Diese Klasse übernimmt 2 Funktionen:
 * <ul>
 * <li>Starten des <Code>ParallelProvider</Code>-Threads. Dieser stellt den
 * eigentlichen Netzwerk-Server dar.
 * <li>Verwaltung der Spiele. Dazu gehören die Aufgaben:
 * <ul>
 * <li>Neue Spiele erstellen
 * <li>Spieler einem Spiel hinzufügen
 * </ul>
 * </ul>
 */
public class GameManager {

	private ArrayList<Game> games = new ArrayList<Game>();

	public GameManager() {
		super();
		ParallelProvider parallelProvider = new ParallelProvider(this);
		Thread t = new Thread(parallelProvider);
		t.setName("parallelProvider");
		t.start();
	}

	/**
	 * Erstellt ein neues <code>Game</code>-Objekt mit dem Namen
	 * <code>gamename</code>. Das Spiel läuft in einem eigenen Thread. Die ID
	 * des erstellten Spiels wird zurück gegeben.
	 * 
	 * @param gamename
	 *            Der Spielename
	 * @return die ID des erstellten Spiels
	 */
	public int createGame(String gamename) {
		Game newGame = new Game(gamename);
		synchronized (games) {
			games.add(newGame);
		}
		Thread t = new Thread(newGame);
		t.setName(gamename);
		t.start();
		return newGame.getGameID();

	}

	/**
	 * Fügt einem Spiel einen Spieler hinzu.
	 * <p>
	 * Fügt dem Spiel mit der ID <code>gameID</code> den Spieler
	 * <code>gamer</code> hinzu. Der Zustand <code>state</code> gibt an welche
	 * Partei der Spieler werden soll:
	 * <ul>
	 * <li>1: der Spieler wird ein Mensch
	 * <li>2: der Spieler wird ein Zombie
	 * <li>einen sonstigen Wert (0 wird empfohlen): Der Spieler wird der Partei
	 * mit den wenigeren Spielern hinzugefügt. Bei gleicher Größe der Parteien
	 * wird er zum Mensch.
	 * </ul>
	 * </p>
	 * 
	 * @param gamer der Spieler der einem Spiel hinzugefügt werden soll
	 * @param gameID die ID des Spiels, wo der Spieler hinzugefügt werden soll
	 * @param state Hinweis auf die Partei des Spielers
	 */
	public void addGamerToGame(Gamer gamer, String gameID, int state) {
		Game oldGame = gamer.getGame();
		if (oldGame != null) {
			oldGame.removeGamer(gamer);
		}
		Game game = getGameByID(gameID);
		game.addGamer(gamer, state);
	}

	/**
	 * gibt ein Spiel zurück das die ID <code>gameID</code> besitzt.
	 * @param gameID die ID des gesuchten Spiels
	 * @return das Spiel mit der ID <code>gameID</code>
	 */
	private Game getGameByID(String gameID) {
		return getGameByID(Integer.parseInt(gameID));
	}

	/**
	 * gibt ein Spiel zurück das die ID <code>gameID</code> besitzt.
	 * @param gameID die ID des gesuchten Spiels
	 * @return das Spiel mit der ID <code>gameID</code>
	 */
	private Game getGameByID(int gameID) {
		ArrayList<Game> gamesClone = getGamesClone();
		for (Game g : gamesClone) {
			if (g.getGameID() == gameID) {
				return g;
			}
		}
		return null;
	}

	public static void main(String[] args) {
		GameManager gm = new GameManager();
	}

	/**
	 * klont die Liste der Spiele, wobei <Code>games</Code> synchronized ist.
	 * 
	 * @return die geklonte Spielerliste
	 */
	public ArrayList<Game> getGamesClone() {
		synchronized (games) {
			return (ArrayList<Game>) games.clone();
		}
	}

}
