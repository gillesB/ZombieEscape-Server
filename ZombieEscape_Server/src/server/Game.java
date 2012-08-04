package server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {

	private int gameID;
	private static AtomicInteger gameIDcounter = new AtomicInteger(0);
	private String name;
	private ArrayList<Gamer> gamers;

	public Game(String gamename) {
		gameID = gameIDcounter.getAndAdd(1);
		this.name = gamename;
		this.gamers = new ArrayList<Gamer>();
	}

	public int getActiveGamersCount() {
		return gamers.size();
	}

	public GPS_location getLocation() {
		GPS_location location = new GPS_location();
		int amountGamers = gamers.size();
		if (amountGamers != 0) {
			for (Gamer g : gamers) {
				GPS_location gamerLocation = g.getLocation();
				location.longitude += gamerLocation.longitude;
				location.latitude += gamerLocation.latitude;
			}
			location.longitude /= amountGamers;
			location.latitude /= amountGamers;
		}
		return location;
	}

	public void addGamer(Gamer gamer) {
		gamers.add(gamer);
		gamer.setGame(this);
	}

	public void removeGamer(Gamer gamer) {
		gamers.remove(gamer);
		gamer.setGame(null);
	}

	public void findInaktivGamers() {

	}

	public void findCollision() {

	}

	public void fight(Gamer g1, Gamer g2) {

	}

	public int getGameID() {
		return gameID;
	}

	public String getName() {
		return name;
	}

}
