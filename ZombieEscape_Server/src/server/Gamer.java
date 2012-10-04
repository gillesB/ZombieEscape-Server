package server;

import java.util.concurrent.atomic.AtomicInteger;

import socket.ProviderTask;

/**
 * Enthält Informationen über einen Spieler. Ein <code>Game</code>-Objekt
 * besitzt eine 1 zu 1 Verbindung mit einem <code>ProviderTask</code>-Objekt.
 * 
 * 
 */
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

	public Fight getFight() {
		return fight;
	}

	/**
	 * Benachrichtigt den Spieler dass er dem <code>Fight</code>-Objekt
	 * <code>fight</code> hinzugefügt wurde, welches Eingabewert ist. Eine
	 * entsprechende Nachricht wird an den Client gesendet. </br> Außnahme: Ist
	 * der Eingabewert <code>null</code>, so wird angenommen dass der
	 * <code>Fight</code> beendet wurde, in dem sich der Spieler befand. Der
	 * Client erhält eine entsprechende Nachricht und den Wert true falls der
	 * Spieler den Kampf überlebt hat, andernfalls false.
	 * 
	 * @param fight Das <code>Fight</code>-Objekt welchem der Spieler hinzugefügt wurde. <code>null</code> wenn der Kampf beendet wurde.
	 */
	void setFight(Fight fight) {
		this.fight = fight;
		if (fight == null) {
			boolean stillAlive = health > 0 ? true : false;
			this.providerTask.fightOver(stillAlive);
		} else {
			this.providerTask.fight();
		}
	
	}

	public int getHealth() {
		return health;
	}

	/**
	 * Zieht den Eingabewert <code>strength</code> von der Gesundheit
	 * <code>helath</code> des Spielers ab.
	 * 
	 * @param strength
	 */
	public void getsDamage(int strength) {
		health -= strength;
	}

	/**
	 * setzt den Gesundheitswert wieder auf den Anfangswert von 100.
	 */
	public void resetHealth() {
		health = 100;
	
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Falls sich der Spieler in einem Spiel befindet, verlässt er dieses.
	 */
	public boolean quitGame() {
		if (game != null) {
			game.removeGamer(this);
			return true;
		} else {
			return false;
		}
		
	}

	public int getGamerID() {
		return gamerID;
	}

	public GPS_location getLocation() {
		return location;
	}

	public void setLocation(GPS_location location) {
		this.location = location;
	}

	public boolean isZombie() {
		return zombie;
	}

	public void setZombie(boolean zombie) {
		this.zombie = zombie;
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

}
