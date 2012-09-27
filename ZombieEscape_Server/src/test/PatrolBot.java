package test;

import java.io.IOException;
import java.util.Random;

import server.GPS_location;
import socket.Socket_GameOverview;

import com.google.gson.JsonSyntaxException;

public class PatrolBot extends AutoNetworkConnection {

	private static int botID = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PatrolBot b = new PatrolBot();
		GPS_location walkFrom = new GPS_location(49.233895, 6.979941);
		GPS_location walkTo = new GPS_location(49.233993, 6.982645);
		try {
			b.openConnection("127.0.0.1");
			Random r = new Random();
			b.newGamer("bot" + r.nextInt(1000));
			b.joinGameBotnet(0);

			b.patrolBetween(walkFrom, walkTo);

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

	/*
	 * private void patrolBetween(GPS_location from, GPS_location to) throws
	 * IOException { setLocation(from); double x = from.longitude; double
	 * direction = from.longitude < to.longitude ? 1 : -1;
	 * 
	 * // equation of a line (Geradengleichung) double numerator1 = to.latitude
	 * - from.latitude; double numerator2 = to.longitude * from.latitude -
	 * from.longitude to.latitude; // TODO avoid division by zero double
	 * denominator = to.longitude - from.longitude;
	 * 
	 * while (true) { try { Thread.sleep(1000); } catch (InterruptedException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * x += (0.00001 * direction); double y = numerator1 / denominator * x +
	 * numerator2 / denominator; setLocation(x, y); if ((to.longitude - x) *
	 * direction < 0) { // swap from and to GPS_location temp = to; to = from;
	 * from = temp; x = from.longitude; direction = from.longitude <
	 * to.longitude ? 1 : -1; System.out.println("changed direction"); } //dump
	 * the input socketIn.readLine(); }
	 */
	private void patrolBetween(GPS_location from, GPS_location to) throws IOException {
		setLocation(from);
		GPS_location target = to;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			setLocation(goInDirection(target, 0.001));
			
			if (myLocation.longitude <= from.longitude || to.longitude <= myLocation.longitude) {
				if(target == to){
					target = from;
				} else {
					target = to;
				}
				System.out.println("changed direction");
			}
			
			// dump the input
			socketIn.readLine();
		}

	}

}
