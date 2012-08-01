package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * makes the actual communication with a client. See Socket Commands in the
 * documentation for more detail.
 * 
 */
public class ProviderTask implements Runnable {
	protected Socket clientSocket = null;
	ObjectInputStream input;
	ObjectOutputStream output;
	String message;
	String[] splitMessage;

	// RecommenderSystem recommender;

	// public ProviderTask(Socket clientSocket, RecommenderSystem recommender) {
	// this.clientSocket = clientSocket;
	// this.recommender = recommender;
	// }

	public void run() {
		try {
			// 3. get Input and Output streams
			input = new ObjectInputStream(clientSocket.getInputStream());
			output = new ObjectOutputStream(clientSocket.getOutputStream());
			do {
				try {
					message = (String) input.readObject();
					splitMessage = message.split(" ");
					System.out.println("client>" + message);

					parseMessage(splitMessage);

				} catch (ClassNotFoundException e) {
					System.err.println("Data received in unknown format");
				} catch (EOFException e) {// occures when client stalls
					break;
				}
			} while (!message.equals("bye"));

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

	/**
	 * @param message
	 */
	private void parseMessage(String[] message) {
		String command = message[0];
		if (true) {
			
		} else if (command.equals("bye")) {
			// do nothing message is handled in run()
		} else {
			System.err.println("Unkown Command: " + command);
		}

	}

	/**
	 * @param msg
	 */
	private void sendMessage(String msg) {
		try {
			output.writeObject(msg);
			output.flush();
			System.out.println("server>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

}
