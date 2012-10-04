package socket;

import java.util.ArrayList;
import java.util.Collection;

import server.GPS_location;
import server.Gamer;

public class Socket_Utils {

	
	public static ArrayList<Socket_GamerOverview> transformGamerslistToSocket_GamerOverviewList(Collection<Gamer> collection){
		ArrayList<Socket_GamerOverview> overview = new ArrayList<Socket_GamerOverview>(collection.size());
		for (Gamer g : collection) {
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
	
	public static ArrayList<Socket_GamerInFight> transformGamerslistToSocket_GamerInFight(Collection<Gamer> collection){
		ArrayList<Socket_GamerInFight> opponents = new ArrayList<Socket_GamerInFight>(collection.size());
		for (Gamer g : collection) {
			Socket_GamerInFight o = new Socket_GamerInFight();
			o.gamerID = new Integer(g.getGamerID()).toString();
			o.gamerName = g.getName();
			o.health = g.getHealth();
			o.isZombie = g.isZombie();
			opponents.add(o);
		}
		return opponents;
	}
	
}
