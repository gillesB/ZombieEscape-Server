package server;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import socket.Socket_GamerOverview;

public class Game implements Runnable {

	private int gameID;
	private static AtomicInteger gameIDcounter = new AtomicInteger(0);
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
			if (state == 0) {
				// does the gamer become a zombie or a human?
				if (zombieCount.get() < humanCount.get()) {
					gamer.setZombie(true);
					zombieCount.getAndIncrement();
				} else {
					gamer.setZombie(false);
					humanCount.getAndIncrement();
				}
			} else if (state == 1) { // gamer becomes human
				gamer.setZombie(false);
			} else { // gamer becomes zombie
				gamer.setZombie(true);
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
			ArrayList<Socket_GamerOverview> overview = new ArrayList<Socket_GamerOverview>(gamersClone.size());
			for (Gamer g : gamersClone) {
				Socket_GamerOverview s = new Socket_GamerOverview();
				s.gamername = g.getName();
				GPS_location gps = g.getLocation();
				s.latitude = gps.latitude;
				s.longitude = gps.longitude;
				s.isZombie = g.isZombie();
				overview.add(s);
			}
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
					// one
					// of
					// the
					// gamers
					// is
					// a
					// zombie
					// and
					// they
					// are
					// near
					// to
					// each
					// other
					fight(g1, g2);
				}
			}
		}
	}

	public void fight(Gamer g1, Gamer g2) {
		// very simple version to begin
		g1.getProviderTask().fight();
		g2.getProviderTask().fight();

		Random r = new Random();
		boolean zombieWins = (r.nextInt() % 2 == 0) ? true : false;
		if (g1.isZombie() && zombieWins || !g1.isZombie() && !zombieWins) {
			g1.fightOutcome(true);
			g2.fightOutcome(false);
		} else {
			g1.fightOutcome(false);
			g2.fightOutcome(true);
		}

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
