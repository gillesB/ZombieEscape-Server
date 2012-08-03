package server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Game {
	
	private int gameID;
	private static AtomicInteger gameIDcounter = new AtomicInteger(0);
	private String name;
	private ArrayList<Gamer> gamers;
	
	//probablyNotNeeded just take gamers.size
	private int activGamers = 0;
	
	public Game(String gamename) {
		gameID = gameIDcounter.getAndAdd(1);
		this.name = gamename;
		this.gamers = new ArrayList<Gamer>();
	}

	public int getActiveGamersCount(){
		return 0;
	}
	
	public GPS_location getLocation(){
		return null;
	}
	
	public void addGamer(Gamer gamer){
		gamers.add(gamer);		
	}
	
	public void removeGamer(Gamer gamer){
		gamers.remove(gamers);
	}
	
	public void findInaktivGamers(){
		
	}
	
	public void findCollision(){
		
	}
	
	public void fight(Gamer g1, Gamer g2){
		
	}

	public int getGameID() {
		return gameID;
	}
	
	
	
	

}
