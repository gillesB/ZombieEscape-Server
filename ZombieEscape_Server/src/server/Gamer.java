package server;

import java.util.concurrent.atomic.AtomicInteger;

import socket.ProviderTask;

public class Gamer {

	private String name;
	private GPS_location location;
	private boolean zombie;
	private final int gamerID;
	private Game game;
	private static AtomicInteger gamerIDcounter = new AtomicInteger(1);
	private ProviderTask providerTask;
	private int health = 100;
	private Fight fight;

	public Gamer(String name, GPS_location location, boolean zombie, ProviderTask providerTask) {
		super();
		this.name = name;
		this.location = location;
		this.gamerID = gamerIDcounter.getAndIncrement();
		this.providerTask = providerTask;
	}

	public Gamer(String name, ProviderTask providerTask) {
		super();
		this.gamerID = gamerIDcounter.getAndAdd(1);
		this.name = name;
		this.location = new GPS_location();
		this.providerTask = providerTask;
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

	public void setZombie(boolean zombie) {
		this.zombie = zombie;
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

	public void setName(String name) {
		this.name = name;
	}

	public void getsDamage(int strength) {
		health -= strength;		
	}

	public Fight getFight() {
		return fight;
	}

	void setFight(Fight fight) {
		this.fight = fight;
		if(fight == null){
			boolean stillAlive = health > 0 ? true : false;
			this.providerTask.fightOver(stillAlive);
		} else {
			this.providerTask.fight();
		}
		
	}

	public int getHealth() {
		return health;
	}

	public void resetHealth() {
		health = 100;
		
	}
	
	
	
	
	
	

}
