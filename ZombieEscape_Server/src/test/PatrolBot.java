package test;

import java.io.IOException;

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
		GPS_location walkFrom = new GPS_location(49.233993, 6.982645);
		GPS_location walkTo = new GPS_location(49.233895, 6.979941);
		try {
			b.openConnection("127.0.0.1");
			b.newGamer("bot"+botID);
			b.joinGameBotnet();
			
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

	private void joinGameBotnet() throws IOException {
		Socket_GameOverview[] games = getGameList();
		boolean gameBotnetExists = false;
		int botnetGameID = -1;
		for (Socket_GameOverview g : games) {
			if (g.name.equals("Botnet")) {
				botnetGameID = g.gameID;
				gameBotnetExists = true;
			}
		}
		if (!gameBotnetExists) {
			botnetGameID = this.newGame("Botnet");
		}
		joinGame(botnetGameID);
	}

	private void patrolBetween(GPS_location from, GPS_location to) {
		setLocation(from);
		double x = from.longitude;
		double direction = from.longitude < to.longitude ? 1 : -1;

		//equation of a line (Geradengleichung)
		double numerator1 = to.latitude - from.latitude;
		double numerator2 = to.longitude * from.latitude - from.longitude * to.latitude;
		//TODO avoid division by zero
		double denominator = to.longitude - from.longitude;

		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			x += (0.00001 * direction);
			double y = numerator1 / denominator * x + numerator2 / denominator;
			setLocation(x,y);
			if((to.longitude-x)*direction < 0 ){
				//swap from and to
				GPS_location temp = to;
				to = from;
				from = temp;
				x = from.longitude;
				direction = from.longitude < to.longitude ? 1 : -1;
				System.out.println("changed direction");
			}
		}

	}
}
