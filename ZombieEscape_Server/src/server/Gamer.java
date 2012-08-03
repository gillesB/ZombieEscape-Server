package server;

import java.util.concurrent.atomic.AtomicInteger;

public class Gamer {

	private String name;
	private GPS_location location;
	private boolean zombie;
	private final int gamerID;
	private Game game;
	private static AtomicInteger gamerIDcounter = new AtomicInteger(0);

	public Gamer(String name, GPS_location location, boolean zombie) {
		super();
		this.name = name;
		this.location = location;
		this.gamerID = gamerIDcounter.getAndAdd(1);
	}

	public Gamer(String name) {
		super();
		this.gamerID = gamerIDcounter.getAndAdd(1);
		this.name = name;
	}

	public void setLocation(GPS_location location) {
		this.location = location;
	}

	public int getGamerID() {
		return gamerID;
	}

	public void quitGame() {
		game.removeGamer(this);
	}

}
