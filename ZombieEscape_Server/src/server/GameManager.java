package server;

import java.util.ArrayList;

import socket.ParallelProvider;

public class GameManager {

	private ArrayList<Game> games = new ArrayList<Game>();

	public GameManager() {
		super();
		ParallelProvider parallelProvider = new ParallelProvider(this);
		new Thread(parallelProvider).start();
	}

	public int createGame(String gamename) {
		Game newGame = new Game(gamename);
		synchronized (games) {
			games.add(newGame);
		}
		new Thread(newGame).start();
		return newGame.getGameID();

	}

	public void closeGame(Game game) {
		synchronized (games) {
			games.remove(game);
		}
	}

	public void closeInactiveGames() {

	}

	public void addGamerToGame(Gamer gamer, String gameID) {
		Game oldGame = gamer.getGame();
		if (oldGame != null) {
			oldGame.removeGamer(gamer);
		}
		Game game = getGameByID(gameID);
		game.addGamer(gamer);
	}

	private Game getGameByID(String gameID) {
		return getGameByID(Integer.parseInt(gameID));
	}

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

	public ArrayList<Game> getGamesClone() {
		synchronized (games) {
			return (ArrayList<Game>) games.clone();
		}
	}

}
