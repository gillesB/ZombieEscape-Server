package socket;

public class Socket_AddGamer {
	String gameID;
	/***
	 * which state should the player become: 0 - random 1 - human 2 - zombie
	 * */
	int state;
	public Socket_AddGamer(String gameID, int state) {
		super();
		this.gameID = gameID;
		this.state = state;
	}
	
	

}
