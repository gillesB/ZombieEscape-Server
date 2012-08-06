package server;

import java.util.concurrent.atomic.AtomicInteger;

import socket.ProviderTask;

public class Gamer {

	private String name;
	private GPS_location location;
	private boolean zombie;
	private final int gamerID;
	private Game game;
	private static AtomicInteger gamerIDcounter = new AtomicInteger(0);
	private ProviderTask providerTask;

	public Gamer(String name, GPS_location location, boolean zombie, ProviderTask providerTask) {
		super();
		this.name = name;
		this.location = location;
		this.gamerID = gamerIDcounter.getAndAdd(1);
		this.providerTask = providerTask;
	}

	public Gamer(String name) {
		super();
		this.gamerID = gamerIDcounter.getAndAdd(1);
		this.name = name;
		this.location = new GPS_location();
	}

	public GPS_location getLocation() {
		return location;
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

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean isZombie() {
		return zombie;
	}

	public void fightOutcome(boolean b) {
		providerTask.fightOver(b);
		if (zombie == false) {
			zombie = b;
		}
	}

	public ProviderTask getProviderTask() {
		return providerTask;
	}

	public String getName() {
		return name;
	}
	
	

}
