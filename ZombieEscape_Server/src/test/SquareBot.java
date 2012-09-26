package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import server.GPS_location;
import socket.SocketMessage;
import socket.Socket_GamerOverview;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.StringMap;

public class SquareBot extends AutoNetworkConnection {

	private GPS_location lowerleftCorner;
	private GPS_location uppertopCorner;
	private boolean zombie;

	public SquareBot(GPS_location lowerleftCorner, GPS_location uppertopCorner) {
		super();
		this.lowerleftCorner = lowerleftCorner;
		this.uppertopCorner = uppertopCorner;
		checkCoordinates();
	}

	public static void main(String[] args) {
		GPS_location ll_corner = new GPS_location(49.233716, 6.975642);
		GPS_location ut_corner = new GPS_location(49.234802, 6.977476);

		SquareBot b = new SquareBot(ll_corner, ut_corner);

		try {
			b.openConnection("127.0.0.1");
			Random r = new Random();
			b.newGamer("bot" + r.nextInt(1000));
			b.zombie = b.joinGameBotnet();
			b.setRandomLocation();
			b.playZombieEscape();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setRandomLocation() {
		Random r = new Random();

		double diffLat = uppertopCorner.latitude - lowerleftCorner.latitude;
		double latitude = lowerleftCorner.latitude + (diffLat * r.nextDouble());

		double diffLong = uppertopCorner.longitude - lowerleftCorner.longitude;
		double longitude = lowerleftCorner.longitude + (diffLong * r.nextDouble());

		setLocation(longitude, latitude);

	}

	private void playZombieEscape() throws IOException {
		while (true) {
			System.out.println("loop");
			// if (zombie) {
			huntHumans();
			/*
			 * } else { escapeZombies(); }
			 */
		}

	}

	/**
	 * get the location of the gamer, who is human and nearest to this gamer
	 * 
	 * @param gamers
	 * @return
	 */
	private GPS_location getLocationOfNearest(Boolean lookingForZombie, ArrayList<StringMap<Socket_GamerOverview>> gamers) {
		double smallestDistance = Double.MAX_VALUE;
		GPS_location nearestGamerLocation = null;
		for (StringMap<Socket_GamerOverview> str_gamer : gamers) {
			Socket_GamerOverview gamer = gson.fromJson(str_gamer.toString(), Socket_GamerOverview.class);
			if (gamer.isZombie && lookingForZombie || !gamer.isZombie && !lookingForZombie ) { // gamer is human
				GPS_location locationOfGamer = new GPS_location(gamer.latitude, gamer.longitude);
				double distance = distanceTo(locationOfGamer);
				if (distance < smallestDistance) {
					smallestDistance = distance;
					nearestGamerLocation = locationOfGamer;
				}
			}
		}
		return nearestGamerLocation;
	}
	
	private GPS_location getLocationOfNearestHuman(ArrayList<StringMap<Socket_GamerOverview>> gamers) {
		return getLocationOfNearest(false, gamers);
	}
	

	private GPS_location getLocationOfNearestZombie(ArrayList<StringMap<Socket_GamerOverview>> gamers) {
		return getLocationOfNearest(true, gamers);
	}

	private void huntHumans() throws IOException {
		SocketMessage message = getMessageFromServer();
		if (message.command.equals("listGamers")) {
			ArrayList<StringMap<Socket_GamerOverview>> gamers = (ArrayList<StringMap<Socket_GamerOverview>>) message.value;
			GPS_location nearestHuman = getLocationOfNearestHuman(gamers);
			setLocation(goInDirection(nearestHuman, 0.001));
		} else {
			System.out.println("got command " + message.command + ", but I ignore it. Value was: " + message.value);
		}

	}

	private void escapeZombies() throws IOException {
		SocketMessage message = getMessageFromServer();
		if (message.command.equals("listGamers")) {
			ArrayList<StringMap<Socket_GamerOverview>> gamers = (ArrayList<StringMap<Socket_GamerOverview>>) message.value;
			GPS_location nearestZombie = getLocationOfNearestZombie(gamers);
			setLocation(goInDirection(nearestZombie, -0.001));
		} else {
			System.out.println("got command " + message.command + ", but I ignore it. Value was: " + message.value);
		}
	}


	private void checkCoordinates() {
		if (lowerleftCorner.longitude >= uppertopCorner.longitude
				|| lowerleftCorner.latitude >= uppertopCorner.latitude) {
			System.err.println("Coordinates notin right order!");
			System.exit(1);
		}
	}

}
