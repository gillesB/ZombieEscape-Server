package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import server.GPS_location;
import socket.SocketMessage;
import socket.Socket_AttackGamer;
import socket.Socket_GamerOverview;
import socket.Socket_Opponent;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.StringMap;

public class SquareBot extends AutoNetworkConnection implements Runnable {

	private GPS_location lowerleftCorner;
	private GPS_location uppertopCorner;
	private boolean zombie;
	private String botname;

	public SquareBot(GPS_location lowerleftCorner, GPS_location uppertopCorner) {
		super();
		this.lowerleftCorner = lowerleftCorner;
		this.uppertopCorner = uppertopCorner;
		checkCoordinates();
		Random r = new Random();
		botname = "bot" + r.nextInt(1000);
		System.out.println(botname);
	}

	@Override
	public void run() {
		try {
			this.playZombieEscape();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException, JsonSyntaxException, IOException {
		// GPS_location ll_corner = new GPS_location(49.233716, 6.975642);
		// GPS_location ut_corner = new GPS_location(49.234802, 6.977476);
		GPS_location ll_corner = new GPS_location(0, 0);
		GPS_location ut_corner = new GPS_location(1, 1);
		// SquareBot b = new SquareBot(ll_corner, ut_corner);
		// Thread t = new Thread(b);
		// t.setName(b.getBotname());
		// t.start();

		SquareBot bh = new SquareBot(ll_corner, ut_corner);
		bh.openConnection("127.0.0.1");
		bh.newGamer(bh.getBotname());
		bh.zombie = !bh.joinGameBotnet(0);
		bh.setLocation(0.5, 0.5);
		new Thread(bh).start();

		/*
		 * SquareBot bz = new SquareBot(ll_corner, ut_corner);
		 * bz.openConnection("127.0.0.1"); bz.newGamer(bz.getBotname());
		 * bz.zombie = !bz.joinGameBotnet(0); bz.setLocation(0.5, 0.5);
		 * bz.playZombieEscape(); new Thread(bz).start();
		 */

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
			if (zombie) {
				huntHumans();
			} else {
				escapeZombies();
			}
		}
	}

	/**
	 * get the location of the gamer, who is human and nearest to this gamer
	 * 
	 * @param gamers
	 * @return
	 */
	private GPS_location getLocationOfNearest(Boolean lookingForZombie,
			ArrayList<StringMap<Socket_GamerOverview>> gamers) {
		double smallestDistance = Double.MAX_VALUE;
		GPS_location nearestGamerLocation = null;
		for (StringMap<Socket_GamerOverview> str_gamer : gamers) {
			Socket_GamerOverview gamer = gson.fromJson(str_gamer.toString(), Socket_GamerOverview.class);
			if (!(gamer.isZombie ^ lookingForZombie)) { // both values must be
				// true or both must
				// false
				GPS_location locationOfGamer = new GPS_location(gamer.latitude, gamer.longitude);
				double distance = myLocation.getDistanceTo_km(locationOfGamer);
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
			System.out.println("next human in " + myLocation.getDistanceTo_km(nearestHuman) + " km. (" + nearestHuman
					+ ")");
			setLocation(goInDirection(nearestHuman, 0.0001));
		} else if (message.command.equals("fight")) {
			fight();
		} else {
			System.out.println("got command " + message.command + ", but I ignore it. Value was: " + message.value);
		}

	}

	private void escapeZombies() throws IOException {
		SocketMessage message = getMessageFromServer();
		if (message.command.equals("listGamers")) {
			ArrayList<StringMap<Socket_GamerOverview>> gamers = (ArrayList<StringMap<Socket_GamerOverview>>) message.value;
			GPS_location nearestZombie = getLocationOfNearestZombie(gamers);
			System.out.println("next zombie in " + myLocation.getDistanceTo_km(nearestZombie) + " km. ("
					+ nearestZombie + ")");
			GPS_location step = goInDirection(nearestZombie, -0.0001);

			// do not run away. Fight for your life!
			/*
			 * if (!inBoundaries(step)) { step =
			 * findValidStepWhichIsInBoundaries(nearestZombie, 0.0001); }
			 * setLocation(step);
			 */

		} else if (message.command.equals("fight")) {
			fight();
		} else {
			System.err.println("got command " + message.command + ", but I ignore it. Value was: " + message.value);
		}
	}

	private void fight() throws IOException {
		while (true) {
			SocketMessage message = getMessageFromServer();
			System.out.println(botname + ": got message " + message.command);
			if (message.command.equals("listOpponents")) {
				System.out.println("with value " + message.value);
				ArrayList<StringMap<Socket_Opponent>> opponents = (ArrayList<StringMap<Socket_Opponent>>) message.value;
				Socket_AttackGamer attackGamer = new Socket_AttackGamer();
				Random r = new Random();
				// choose opponent randomly
				StringMap<Socket_Opponent> str_attackOpponent = opponents.get(r.nextInt(opponents.size()));
				Socket_Opponent attackOpponent = gson.fromJson(str_attackOpponent.toString(), Socket_Opponent.class);

				attackGamer.IDofAttackedGamer = attackOpponent.gamerID;
				attackGamer.strength = r.nextInt(25);
				System.out.println(botname + ": I attack " + attackGamer.IDofAttackedGamer + " with strength: "
						+ attackGamer.strength);
				
				sendJSONObject(new SocketMessage("attack", attackGamer));
			} else if (message.command.equals("fightOver")) {
				boolean stillAlive = gson.fromJson(message.value.toString(), Boolean.class);
				if (!stillAlive) {
					sendJSONObject(new SocketMessage("bye"));
					System.out.println("I am dead.");
					System.exit(1);
				} else {
					System.out.println("I am still alive.");
					break;
				}
			} else {
				System.err.println("in fight: got command " + message.command + ", but I ignore it. Value was: "
						+ message.value);
			}
		}
	}

	private boolean inBoundaries(GPS_location step) {
		if (step.latitude >= lowerleftCorner.latitude && step.longitude >= lowerleftCorner.longitude
				&& step.latitude <= uppertopCorner.latitude && step.longitude <= uppertopCorner.longitude) {
			return true;
		} else {
			return false;
		}
	}

	// TODO very ugly
	private GPS_location findValidStepWhichIsInBoundaries(GPS_location nearestZombie, double stepSize) {
		double maxDistanceToZombie = 0;
		GPS_location makeStep = myLocation;

		// check north
		GPS_location reachableLocation = new GPS_location(myLocation.latitude + stepSize, myLocation.longitude);
		if (inBoundaries(reachableLocation)) {
			maxDistanceToZombie = nearestZombie.getDistanceTo_km(reachableLocation);
		}

		// check south
		reachableLocation = new GPS_location(myLocation.latitude - stepSize, myLocation.longitude);
		double distanceToZombie = nearestZombie.getDistanceTo_km(reachableLocation);
		if (maxDistanceToZombie < distanceToZombie && inBoundaries(reachableLocation)) {
			maxDistanceToZombie = distanceToZombie;
			makeStep = reachableLocation.copy();
		}

		// check east
		reachableLocation = new GPS_location(myLocation.latitude, myLocation.longitude + stepSize);
		distanceToZombie = nearestZombie.getDistanceTo_km(reachableLocation);
		if (maxDistanceToZombie < distanceToZombie && inBoundaries(reachableLocation)) {
			maxDistanceToZombie = distanceToZombie;
			makeStep = reachableLocation.copy();
		}

		// check west
		reachableLocation = new GPS_location(myLocation.latitude, myLocation.longitude - stepSize);
		distanceToZombie = nearestZombie.getDistanceTo_km(reachableLocation);
		if (maxDistanceToZombie < distanceToZombie && inBoundaries(reachableLocation)) {
			maxDistanceToZombie = distanceToZombie;
			makeStep = reachableLocation.copy();
		}

		return makeStep;
	}

	private void checkCoordinates() {
		if (lowerleftCorner.longitude >= uppertopCorner.longitude
				|| lowerleftCorner.latitude >= uppertopCorner.latitude) {
			System.err.println("Coordinates not in right order!");
			System.exit(2);
		}
	}

	public String getBotname() {
		return botname;
	}

}
