package socket;

import java.util.ArrayList;

import server.GPS_location;
import server.Gamer;

public class Socket_Utils {

	
	public static ArrayList<Socket_GamerOverview> transformGamerslistToSocket_GamerOverviewList(ArrayList<Gamer> gamers){
		ArrayList<Socket_GamerOverview> overview = new ArrayList<Socket_GamerOverview>(gamers.size());
		for (Gamer g : gamers) {
			Socket_GamerOverview s = new Socket_GamerOverview();
			s.gamername = g.getName();
			GPS_location gps = g.getLocation();
			s.latitude = gps.latitude;
			s.longitude = gps.longitude;
			s.isZombie = g.isZombie();
			overview.add(s);
		}
		return overview;
	}
	
}
