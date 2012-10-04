package socket;

import java.util.ArrayList;
import java.util.Collection;

import server.GPS_location;
import server.Game;
import server.Gamer;

public class Socket_Utils {

	public static ArrayList<Socket_GamerOverview> transformGamersListToSocket_GamerOverviewList(Collection<Gamer> gamers) {
		ArrayList<Socket_GamerOverview> overview = new ArrayList<Socket_GamerOverview>(gamers.size());
		for (Gamer g : gamers) {
			Socket_GamerOverview s = new Socket_GamerOverview();
			s.gamername = g.getName();
			GPS_location gps = g.getLocation();
			s.latitude = gps.latitude;
			s.longitude = gps.longitude;
			s.health = g.getHealth();
			s.isZombie = g.isZombie();
			overview.add(s);
		}
		return overview;
	}

	public static ArrayList<Socket_GamerInFight> transformGamersListToSocket_GamerInFightList(Collection<Gamer> gamers) {
		ArrayList<Socket_GamerInFight> sock_gamers = new ArrayList<Socket_GamerInFight>(gamers.size());
		for (Gamer g : gamers) {
			Socket_GamerInFight o = new Socket_GamerInFight();
			o.gamerID = new Integer(g.getGamerID()).toString();
			o.gamerName = g.getName();
			o.health = g.getHealth();
			o.isZombie = g.isZombie();
			sock_gamers.add(o);
		}
		return sock_gamers;
	}

	public static ArrayList<Socket_GameOverview> transformGameListToSocket_GameOverviewList(ArrayList<Game> currentGames) {
		ArrayList<Socket_GameOverview> gameList = new ArrayList<Socket_GameOverview>(currentGames.size());
		for (Game g : currentGames) {
			Socket_GameOverview go = new Socket_GameOverview();
			go.amountGamers = g.getActiveGamersCount();
			go.gameID = g.getGameID();
			go.name = g.getName();
			GPS_location gps = g.getAverageLocation();
			go.longitude = gps.longitude;
			go.latitude = gps.latitude;
			gameList.add(go);
		}
		return gameList;
	}

}
