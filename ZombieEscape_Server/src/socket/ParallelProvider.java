package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.GameManager;

/**
 * Listens on port 2004, if some clients connect and provides them a ProviderTask
 * 
 *
 */
public class ParallelProvider implements Runnable {

	protected int serverPort = 2004;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread currentThread = null;
	protected ExecutorService threadPool = Executors.newFixedThreadPool(100);
	GameManager gameManager;

	public ParallelProvider(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	@Override
	public void run() {
		synchronized (this) {
			this.currentThread = Thread.currentThread();
		}
		// 1. creating a server socket
		openServerSocket();
		
		while (!isStopped()) {
			// 2. Wait for connection
			System.out.println("Waiting for connection");
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection",e);
			}
			System.out.println("Connection received from "+ clientSocket.getInetAddress().getHostName());
			this.threadPool.execute(new ProviderTask(clientSocket, gameManager));
		}
		
		this.threadPool.shutdown();
		System.out.println("Server Stopped.");
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error while stopping", e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot bind port " + serverPort, e);
		}
	}

}
