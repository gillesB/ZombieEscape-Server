package server;

import java.util.ArrayList;

public class GameManager {
	
	private ArrayList<Game> games;
	
	public void createGame(String gamename){
		games.add(new Game(gamename));		
	}
	
	public void closeGame(Game game){
		games.remove(game);
	}
	
	public void closeInactivGames(){
		
	}
	
	public Object getGames(){
		return null;
	}
	
	public void addGamerToGame(Gamer gamer, String gameID){
		Game game = getGameByID(gameID);
		game.addGamer(gamer);
	}

	private Game getGameByID(String gameID) {
		return getGameByID(Integer.parseInt(gameID));
	}

	private Game getGameByID(int gameID) {
		for(Game g : games){
			if(g.getGameID() == gameID){
				return g;
			}
		}
		return null;
	}
	
	

}
