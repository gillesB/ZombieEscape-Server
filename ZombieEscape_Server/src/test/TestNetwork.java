package test;

import java.io.IOException;

import server.GPS_location;
import socket.SocketMessage;


/**
 * A singleton class, which is accessible through the whole client. It is the
 * only class which has a direct connection to the server. It implements the
 * socket commands, described in the document of Rosebud
 * 
 * 
 */
public class TestNetwork extends AutoNetworkConnection {

	private String username;

	// Private constructor prevents instantiation from other classes
	private TestNetwork() {
		System.out.println("NetworkSingleton started");
	}

	public static void main(String[] args) {
		TestNetwork tn = new TestNetwork();
		try {
			//connect to the server
			tn.openConnection("127.0.0.1");
			
			//create a new gamer on the server, this step has to be done everytime, as the gamers are not saved
			tn.sendJSONObject(new SocketMessage("newGamer","master xardas"));
			
			//get the gamerIDm till now it is not needed, but could be usefull in the future
			int gamerID = tn.gson.fromJson(tn.socketIn.readLine(), Integer.class);
			System.out.println(gamerID);
			
			//add 2 new games
			tn.sendJSONObject(new SocketMessage("newGame","game1"));
			int game1ID = tn.gson.fromJson(tn.socketIn.readLine(), Integer.class);			
			tn.sendJSONObject(new SocketMessage("newGame","game2"));
			int game2ID = tn.gson.fromJson(tn.socketIn.readLine(), Integer.class);
			
			//list existing games, same code can be found in printGameList()
			tn.sendJSONObject(new SocketMessage("listGames"));
			String gamelist =  tn.socketIn.readLine();
			System.out.println(gamelist);
			
			//join own game
			tn.sendJSONObject(new SocketMessage("addGamer", ((Integer)game1ID).toString() ));
			boolean human = tn.gson.fromJson(tn.socketIn.readLine(), Boolean.class);
			tn.printJoinedGameMessage(human);
			tn.sendJSONObject(new SocketMessage("addGamer", ((Integer)game2ID).toString() ));
			human = tn.gson.fromJson(tn.socketIn.readLine(), Boolean.class);		
			tn.printJoinedGameMessage(human);
			tn.getGameList();
			
			//set location
			GPS_location location = new GPS_location(42.0, 42.0);
			tn.sendJSONObject(new SocketMessage("setLocation", location));			
			tn.getGameList();
			
			//remove from game
			tn.sendJSONObject(new SocketMessage("removeGamer"));
			boolean removed = tn.gson.fromJson(tn.socketIn.readLine(), Boolean.class);
			tn.getGameList();
			
			//test if messages from server are received
			tn.sendJSONObject(new SocketMessage("addGamer", ((Integer)game1ID).toString() ));
			human = tn.gson.fromJson(tn.socketIn.readLine(), Boolean.class);
			tn.printJoinedGameMessage(human);
			for(int i = 0; i <= 5; i++){
				Thread.sleep(1000);
				if(tn.socketIn.ready()){
					System.out.println(tn.socketIn.readLine());
				}
				
			}
			
			
			//say good bye
			tn.sendJSONObject(new SocketMessage("bye"));
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	


}
