package server;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import socket.Socket_GamerOverview;
import socket.Socket_Utils;

public class Game implements Runnable {

	private int gameID;
	private static AtomicInteger gameIDcounter = new AtomicInteger(1);
	private static AtomicInteger zombieCount = new AtomicInteger(0);
	private static AtomicInteger humanCount = new AtomicInteger(0);
	private String name;
	private ArrayList<Gamer> gamers;

	public Game(String gamename) {
		gameID = gameIDcounter.getAndIncrement();
		this.name = gamename;
		this.gamers = new ArrayList<Gamer>();
	}

	public int getActiveGamersCount() {
		return gamers.size();
	}

	public GPS_location getLocation() {
		GPS_location location = new GPS_location();
		ArrayList<Gamer> gamersClone = getGamersClone();

		int amountGamers = gamersClone.size();
		if (amountGamers != 0) {
			for (Gamer g : gamersClone) {
				GPS_location gamerLocation = g.getLocation();
				location.longitude += gamerLocation.longitude;
				location.latitude += gamerLocation.latitude;
			}
			location.longitude /= amountGamers;
			location.latitude /= amountGamers;
		}
		return location;
	}

	public void addGamer(Gamer gamer, int state) {
		synchronized (gamers) {
			gamers.add(gamer);
		}
		synchronized (gamer) {
			if (state == 1) { // gamer becomes human
				gamer.setZombie(false);
			} else if (state == 2) { // gamer becomes zombie
				gamer.setZombie(true);
			} else {
				// does the gamer become a zombie or a human?
				if (zombieCount.get() < humanCount.get()) {
					gamer.setZombie(true);
					zombieCount.getAndIncrement();
				} else {
					gamer.setZombie(false);
					humanCount.getAndIncrement();
				}
				if (state != 0) {
					System.err.println("Invalid state in addGamer(): " + state);
				}
			}
			gamer.setGame(this);
		}
	}

	public void removeGamer(Gamer gamer) {
		synchronized (gamers) {
			gamers.remove(gamer);
		}
		synchronized (gamer) {
			if (gamer.isZombie()) {
				zombieCount.getAndDecrement();
			} else {
				humanCount.getAndDecrement();
			}
			gamer.setGame(null);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// TODO change this back
			ArrayList<Gamer> gamersClone = getGamersClone();
			findCollision(gamersClone);
			ArrayList<Socket_GamerOverview> overview = Socket_Utils.transformGamerslistToSocket_GamerOverviewList(gamersClone);
			for (Gamer g : gamersClone) {
				g.getProviderTask().listGamers(overview);
			}
		}
	}

	public void findInaktivGamers() {

	}

	public void findCollision(ArrayList<Gamer> gamersClone) {
		for (int i = 0; i < gamersClone.size() - 1; i++) {
			Gamer g1 = gamersClone.get(i);
			for (int j = i + 1; j < gamersClone.size(); j++) {
				Gamer g2 = gamersClone.get(j);
				if ((g1.isZombie() ^ g2.isZombie()) && g1.getLocation().getDistanceTo_km(g2.getLocation()) < 0.005) { // only
					// one of the gamers is a zombie and they are near to each
					// other
					fight(g1, g2);
				}
			}
		}
	}

	public void fight(Gamer g1, Gamer g2) {
		
		Fight f1 = g1.getFight();
		Fight f2 = g2.getFight();
		
		if(f1 == null && f2 == null){
			Fight newFight = new Fight();
			newFight.addGamer(g1);
			newFight.addGamer(g2);
		} else if(f1 == f2){
			//do nothing
		} else if(f1 != null && f2 == null){
			f1.addGamer(g2);
		} else if(f1 == null && f2 != null){
			f2.addGamer(g1);
		} else {
			System.err.println("This should not happen: f1 != null and f2 != null and f1 != f2");
		}
		
		
		
		
		/*// very simple version to begin
		g1.getProviderTask().fight();
		g2.getProviderTask().fight();
		
		//TODO add class Fight somehow

		Random r = new Random();
		boolean zombieWins = (r.nextInt() % 2 == 0) ? true : false;
		if (g1.isZombie() && zombieWins || !g1.isZombie() && !zombieWins) {
			g1.fightOutcome(true);
			g2.fightOutcome(false);
		} else {
			g1.fightOutcome(false);
			g2.fightOutcome(true);
		}*/

	}

	public int getGameID() {
		return gameID;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Gamer> getGamersClone() {
		synchronized (gamers) {
			return (ArrayList<Gamer>) gamers.clone();
		}
	}

}
