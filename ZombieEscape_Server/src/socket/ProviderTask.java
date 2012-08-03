package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import server.GameManager;
import server.Gamer;

import com.google.gson.Gson;

/**
 * makes the actual communication with a client. See Socket Commands in the
 * documentation for more detail.
 * 
 */
public class ProviderTask implements Runnable {
	protected Socket clientSocket = null;
	BufferedReader input;
	BufferedWriter output;
	Gson gson = new Gson();
	GameManager gameManager;

	Gamer gamer;

	// RecommenderSystem recommender;

	// public ProviderTask(Socket clientSocket, RecommenderSystem recommender) {
	// this.clientSocket = clientSocket;
	// this.recommender = recommender;
	// }

	@Override
	public void run() {
		try {
			// 3. get Input and Output streams
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

			newGamer(input.readLine());
			SocketMessage message = new SocketMessage();
			do {
				try {
					message = gson(input.readLine(), SocketMessage.class);

					System.out.println("client>" + message.command);

					parseMessage(message);

				} catch (EOFException e) {// occures when client stalls
					break;
				}
			} while (!message.command.equals("bye"));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
				input.close();
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private SocketMessage gson(String readLine, Class<SocketMessage> class1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param message
	 */
	private void parseMessage(SocketMessage message) {
		switch (message.command) {
		case ("newGame"):
			newGame();
			break;
		case ("listGames"):
			listGames();
			break;
		case ("addGamer"):
			addGamer(message.value);
			break;
		case ("removeGamer"):
			removeGamer();
			break;
		case ("setLocation"):
			setLocation();
			break;
		default:
			System.err.println("Unkown Command: " + message.command);
		}
	}

	private void removeGamer() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param msg
	 */
	private void sendJSONObject(Object obj) {
		try {
			String json = gson.toJson(obj);
			output.write(json);
			output.flush();
			System.out.println("server>" + json);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private void addGamer(Object json) {
		String gameID = (String) json;
		gameManager.addGamerToGame(gamer, gameID);
	
	}

	private void listGames() {
		// TODO Auto-generated method stub
	
	}

	private void newGamer(String message) {
		SocketMessage newGamer = gson.fromJson(message, SocketMessage.class);
		int gamerID;
		if (newGamer.command.equals("newGamer")) {
			gamer = new Gamer((String) newGamer.value);
			gamerID = gamer.getGamerID();
		} else {
			gamerID = -1;
		}
		sendJSONObject(gamerID);
	}

	private void newGame() {
		// TODO Auto-generated method stub

	}

	private void setLocation() {
		// TODO Auto-generated method stub
	
	}

}
