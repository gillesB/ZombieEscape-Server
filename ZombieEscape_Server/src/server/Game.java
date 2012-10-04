package server;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import socket.Socket_GamerOverview;
import socket.Socket_Utils;

/**
 * Ein Objekt dieser Klasse läuft in einem eigenen Thread und wurde in einem
 * Objekt der Klasse <code>GameManager</code> erstellt. Die Klasse kümmert sich
 * um die Spielerverwaltung, der Spieler die sich im Spiel befinden. Zu den
 * Aufagben gehören:
 * <ul>
 * <li>hinzufügen von Spielern und entscheiden ob er Zombie oder Mensch wird
 * <li>entfernen von Spielern
 * <li>bestimmen ob Spieler gegeneinander kämpfen müssen
 * <li>Spieler einem <Code>Fight</Fight> hinzufügen
 * </ul>
 * 
 * 
 * Das Spiel stoppt sich nie selbst, sonder kann höchsten von außen gestoppt werden.
 */

public class Game implements Runnable {

	private int gameID;
	private static AtomicInteger gameIDcounter = new AtomicInteger(1);
	private static AtomicInteger zombieCount = new AtomicInteger(0);
	private static AtomicInteger humanCount = new AtomicInteger(0);
	private String name;
	private ArrayList<Gamer> gamers;

	public Game(String gamename) {
		gameID = gameIDcounter.getAndIncrement();
		this.name = gamename;
		this.gamers = new ArrayList<Gamer>();
	}

	/**
	 * Gibt die Anzahl der Spieler im Spiel zurück
	 * 
	 * @return Anzahl der Spieler im Spiel
	 */
	public int getActiveGamersCount() {
		return gamers.size();
	}

	/**
	 * Berechnet den Mittelwert der Positionen der Spieler. Den Mittelwert kann
	 * z.B. im Client benutzt werden, um dem Benutzer anzuzeigen, wie weit die
	 * Spieler (in etwa) von ihm entfernt sind. (Es erfolgt die Annahme dass
	 * alle Spieler sich mehr oder weniger an einem Ort (z.B. eine Stadt)
	 * befinden.)
	 * 
	 * @return eine Position, errechnet aus dem Mittelwert der Positionen der
	 *         Spieler
	 */
	public GPS_location getAverageLocation() {
		GPS_location location = new GPS_location();
		ArrayList<Gamer> gamersClone = getGamersClone();

		int amountGamers = gamersClone.size();
		if (amountGamers != 0) {
			for (Gamer g : gamersClone) {
				GPS_location gamerLocation = g.getLocation();
				location.longitude += gamerLocation.longitude;
				location.latitude += gamerLocation.latitude;
			}
			location.longitude /= amountGamers;
			location.latitude /= amountGamers;
		}
		return location;
	}

	/**
	 * Fügt einen Spieler dem Spiel hinzu und bestimmt ob er Zombie oder Mensch
	 * wird.
	 * <p>
	 * Die Eingabewerte sind der Spieler selbst und einen Zustand, dieser
	 * beeinflusst welcher Partei der Spieler hinzugefügt wird. Der Zustand kann
	 * folgende Werte haben:
	 * <ul>
	 * <li>1: der Spieler wird ein Mensch
	 * <li>2: der Spieler wird ein Zombie
	 * <li>einen sonstigen Wert (0 wird empfohlen): Der Spieler wird der Partei
	 * mit den wenigeren Spielern hinzugefügt. Bei gleicher Größe der Parteien
	 * wird er zum Mensch.
	 * </ul>
	 * </p>
	 * 
	 * @param gamer
	 *            der Spieler, der dem Spiel hinzugefügt werden soll
	 * @param state
	 *            Hinweis, welcher Partei der Spieler hinzugefügt werden soll
	 */
	public void addGamer(Gamer gamer, int state) {
		synchronized (gamers) {
			gamers.add(gamer);
			gamer.resetHealth();
		}
		synchronized (gamer) {
			if (state == 1) { // gamer becomes human
				gamer.setZombie(false);
				humanCount.getAndIncrement();
			} else if (state == 2) { // gamer becomes zombie
				gamer.setZombie(true);
				zombieCount.getAndIncrement();
			} else {
				// does the gamer become a zombie or a human?
				System.out.println("zombieCount.get() < humanCount.get() " + zombieCount.get() + " < " + humanCount.get());
				if (zombieCount.get() < humanCount.get()) {
					gamer.setZombie(true);
					zombieCount.getAndIncrement();
				} else {
					gamer.setZombie(false);
					humanCount.getAndIncrement();
				}
				if (state != 0) {
					System.err.println("state in addGamer() is not 0,1 or 2: " + state);
				}
			}
			gamer.setGame(this);
		}
	}

	/**
	 * entfernt einen Spieler aus dem Spiel
	 * 
	 * @param gamer
	 *            der Spieler der entfernt werden soll
	 */
	public void removeGamer(Gamer gamer) {
		synchronized (gamers) {
			gamers.remove(gamer);
		}
		synchronized (gamer) {
			if (gamer.isZombie()) {
				zombieCount.getAndDecrement();
			} else {
				humanCount.getAndDecrement();
			}
			gamer.setGame(null);
		}
	}

	/**
	 * Die Spielschleife des Spiels, hierbei handelt es sich um eine
	 * Endlosschleife. Ein Spiel stoppt sich nie selbst, sondern kann nur von
	 * außen gestoppt werden. Während einer Iteration,
	 * wird eine Sekunde gewartet, dann wird überprüft, ob 2 Spieler nah
	 * aneinander sind. Ist dies der Fall und sie gehören unterschiedlichen
	 * Parteien an, so werden sie einem <code>Fight</code> hinzugefügt.
	 * Weiterhin werden die Spielerpositionen der Spieler an die Spieler
	 * versendet, die sich nicht in einem Kampf befinden.
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ArrayList<Gamer> gamersClone = getGamersClone();
			findCollision(gamersClone);
			ArrayList<Socket_GamerOverview> overview = Socket_Utils.transformGamerslistToSocket_GamerOverviewList(gamersClone);
			for (Gamer g : gamersClone) {
				// only send location updates of the other gamers to Gamer g, if
				// Gamer g is not fighting
				if (g.getFight() == null) {
					g.getProviderTask().listGamers(overview);
				}
			}
		}
	}

	/**
	 * Findet Kollisionen zwischen 2 Spieler aus einer Liste und fügt sie einem
	 * Kampf hinzu.
	 * <p>
	 * Vergleicht die Position eines jeden Spielers, mit der Position von jedem
	 * anderen Spieler. Gehören die Spieler unterschiedlichen Parteien an, so
	 * wird deren Distanz zueinander bestimmt. Ist die Distanz kleiner gleich 5
	 * Meter so werden die Spieler einem <code>Fight</code> hinzugefügt.
	 * </p>
	 * 
	 * @param gamers
	 *            Liste mit den Spielern, für die die Kollision bestimmt wird
	 */
	private void findCollision(ArrayList<Gamer> gamers) {
		for (int i = 0; i < gamers.size() - 1; i++) {
			Gamer g1 = gamers.get(i);
			for (int j = i + 1; j < gamers.size(); j++) {
				Gamer g2 = gamers.get(j);
				if ((g1.isZombie() ^ g2.isZombie()) && g1.getLocation().getDistanceTo_km(g2.getLocation()) <= 0.005) { // only
					// one of the gamers is a zombie and they are near to each
					// other
					fight(g1, g2);
				}
			}
		}
	}

	/**
	 * Erhält 2 Spieler die einem Objekt der Klasse <code>Fight</code>
	 * hinzugefügt werden.
	 * <p>
	 * Holt sich die Kämpfe der beiden Spieler und unterscheidet zwischen
	 * verschiedenen Fällen.
	 * <ul>
	 * <li>Befindet sich keiner der beiden Spieler in einem Kampf, so wird ein
	 * neues <code>Fight</code>-Objekt angelegt und die Spieler werden diesem
	 * Objekt hinzugefügt. Das <code>Fight</code>-Objekt läuft in einem eigenen
	 * Thread.
	 * <li>Befindet sich bereits ein Spieler in einem Kampf, so wird der andere
	 * Spieler diesem hinzugefügt.
	 * <li>Befinden sich beide Spieler in einem Kampf wird nichts gemacht.
	 * </ul>
	 * </p>
	 * 
	 * @param g1
	 *            der erste Spieler
	 * @param g2
	 *            der zweite Spieler
	 */
	private void fight(Gamer g1, Gamer g2) {

		Fight f1 = g1.getFight();
		Fight f2 = g2.getFight();

		if (f1 == null && f2 == null) {
			Fight newFight = new Fight();
			newFight.addGamer(g1);
			newFight.addGamer(g2);
			new Thread(newFight).start();
		} else if (f1 == f2) {
			// do nothing
		} else if (f1 != null && f2 == null) {
			f1.addGamer(g2);
		} else if (f1 == null && f2 != null) {
			f2.addGamer(g1);
		} else {
			System.err.println("This should not happen: f1 != null and f2 != null and f1 != f2");
		}

	}

	/**
	 * Gibt die ID des Spiels zurück
	 * 
	 * @return ID des Spiels
	 */
	public int getGameID() {
		return gameID;
	}

	/**
	 * Gibt den Name des Spiels zurück
	 * 
	 * @return Name des Spiels
	 */
	public String getName() {
		return name;
	}

	/**
	 * klont die Liste der Spieler, wobei <Code>gamers</Code> synchronized ist.
	 * 
	 * @return die geklonte Spielerliste
	 */
	private ArrayList<Gamer> getGamersClone() {
		synchronized (gamers) {
			return (ArrayList<Gamer>) gamers.clone();
		}
	}

}
