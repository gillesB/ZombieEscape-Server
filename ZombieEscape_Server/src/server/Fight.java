package server;

import java.util.ArrayList;
import java.util.HashMap;

import socket.Socket_AttackGamer;
import socket.Socket_GamerInFight;
import socket.Socket_Utils;

/**
 * Ein Objekt dieser Klasse läuft in einem eigenen Thread und wurde in einem
 * Objekt der Klasse <code>Game</code> erstellt. Ein Kampf ist rundenbasiert und
 * jeder Spieler darf einmal pro Runde einen Gegenspieler angreifen. Ehe die
 * Spieler dem eigentlichen Kampf hinzugefügt werden, müssen sie in einer
 * Warteliste warten. Vor jeder Runde wird diese geleert und die Spieler werden
 * ihren jeweiligen Parteien (Menschen oder Zombies) hinzugefügt. </br> Eine
 * Runde hat folgenden Ablauf:
 * <ul>
 * <li>Jeder Spieler der sich im Kampf befindet, erhält die komplette Liste mit
 * den anderen Spielern im Kampf
 * <li>Es wird auf den Angriff der einzelnen Spieler gewartet und dieser wird
 * verrechnet.
 * <li>Die Spieler die während der Runde gtestorben sind, werden auf die Liste
 * mit den toten Spielern gesetzt und können ab der nächsten Runde nicht mehr am
 * Kampf teilnehmen.
 * </ul>
 * Der Kampf ist beendet sobald alle Zombies oder alle Menschen tot sind.
 * 
 */
public class Fight implements Runnable {

	private HashMap<String, Gamer> zombies;
	private HashMap<String, Gamer> humans;
	private ArrayList<Gamer> queue;
	private ArrayList<Gamer> deadGamers;

	public Fight() {
		zombies = new HashMap<String, Gamer>();
		humans = new HashMap<String, Gamer>();
		queue = new ArrayList<Gamer>();
		deadGamers = new ArrayList<Gamer>();
		System.out.println("created new fight");
	}

	/**
	 * Ein Spieler wird in die Warteschlange gesetzt. Zu Beginn der nächsten
	 * Runde kann er dann aktiv im Kampf beitragen.
	 * 
	 * @param gamer
	 *            Der Spieler der in die Warteschlange gesetzt wird.
	 */
	public void addGamer(Gamer gamer) {
		System.out.println("add gamer to fight: " + gamer.getName());
		synchronized (queue) {
			queue.add(gamer);
		}
		gamer.setFight(this);
	}

	/**
	 * Die Spielschleife des Kampfes. Vor jeder Runde werden die Spieler von der
	 * Warteliste in den Kampf hinzugefügt. Danach wird überprüft ob der Kampf
	 * beendet ist. Ist dies nicht der Fall wird eine neue Runde gestartet. Ist
	 * dies der Fall werden die Spieler über das Ende des Kampfes
	 * benachrichtigt.
	 */
	@Override
	public void run() {
		System.out.println("started fight");

		while (true) {
			addQueuedGamersToFight();

			// breaks if all zombies or all humans are dead
			if (zombies.isEmpty() || humans.isEmpty()) {
				break;
			}

			makeARound();
		}

		fightOver();
	}

	/**
	 * eine einzelne Runde des Kampfes. Jeder Spiler darf einmal pro Runde
	 * angreifen, selbst wenn er während dieser Runde stirbt.
	 */
	private void makeARound() {
		System.out.println("entered round");
		// Senden der Spielerliste an die Spieler
		// Order does not matter at the moment
		letZombiesAttack();
		letHumansAttack();

		// warten und verwerten des Angriffes der Spieler
		getZombiesAttackResults();
		getHumansAttackResults();

		// tote Spieler aus dem Kampf nehmen.
		removeDeadZombies();
		removeDeadHumans();
	}

	/**
	 * Benachrichten der Spieler, dass der Kampf vorbei ist. D.h. Spieler aus
	 * dem Kampf nehmen. Tote Spieler werden aus dem Spiel geworfen, in dem der
	 * Kampf gestartet wurde.
	 */
	private void fightOver() {
		for (Gamer g : zombies.values()) {
			g.setFight(null);
		}
		for (Gamer g : humans.values()) {
			g.setFight(null);
		}
		for (Gamer g : deadGamers) {
			g.setFight(null);
			// Tote Spieler werden aus dem Spiel geworfen, in dem der Kampf
			// gestartet wurde.
			g.quitGame();
		}
		synchronized (queue) {
			for (Gamer g : queue) {
				g.setFight(null);
			}
		}
	}

	/**
	 * Die Spieler werden aus der Warteschleife in den Kampf hinzugefügt. Dabei
	 * kommen sie in die Liste die ihre Partei wiederspiegelt (Zombie oder
	 * Mensch).
	 */
	private void addQueuedGamersToFight() {
		synchronized (queue) {
			for (Gamer g : queue) {
				String ID = new Integer(g.getGamerID()).toString();
				if (g.isZombie()) {
					zombies.put(ID, g);
				} else {
					humans.put(ID, g);
				}
			}
			queue.clear();
		}
	}

	/**
	 * Senden der Gegner und Partner an einen Angreifer. Eingabe sind 2
	 * <Code>Gamer</Code>-Listen. Eine enthält die Angreifer, die andere deren
	 * Gegner. Diese beiden Listen werden dann an den Client gesendet. </br>
	 * Bemerkung: Im Augenblick werden die beiden Listen aneinander gehängt.
	 * Ursprünglich sollten die 2 Listen einzeln an die Clients geschickt
	 * werden. Hier gab es allerdings Probleme mit dem iPad-Client.
	 * 
	 * @param attackers
	 *            Die Liste mit den Angreifern
	 * @param opponents
	 *            Die Liste mit den Gegnern
	 */
	private void letAttack(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		ArrayList<Socket_GamerInFight> sock_opponents = Socket_Utils.transformGamersListToSocket_GamerInFightList(opponents.values());
		ArrayList<Socket_GamerInFight> sock_allies = Socket_Utils.transformGamersListToSocket_GamerInFightList(attackers.values());

		// concatenate both lists
		ArrayList<Socket_GamerInFight> fightingGamers = new ArrayList<Socket_GamerInFight>(sock_allies);
		fightingGamers.addAll(sock_opponents);

		// send lists to gamers
		for (Gamer g : attackers.values()) {
			g.getProviderTask().listFightingGamers(fightingGamers);
		}
	}

	/**
	 * senden der Daten an die Zombie-Clients, die dieser benötigt um einen
	 * Menschen anzugreifen. Es werden die Gegner und Parter gesendet.
	 */
	private void letZombiesAttack() {
		letAttack(zombies, humans);
	}

	/**
	 * senden der Daten an die Menschen-Clients, die dieser benötigt um einen
	 * Zombie anzugreifen. Es werden die Gegner und Parter gesendet.
	 */
	private void letHumansAttack() {
		letAttack(humans, zombies);
	}

	/**
	 * Läuft durch die Liste der Angreifer. Wartet bis der Angriff eines
	 * einzelnen Spielers vorhanden ist. Danach wird der vom Client ausgewählte
	 * Spieler mit dem übertragenen Angriffswert angegriffen.
	 * 
	 * @param attackers
	 *            Liste der Angreifer
	 * @param opponents
	 *            Liste der angegriffenen Spieler
	 */
	private void getAttackResults(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		for (Gamer g : attackers.values()) {
			Socket_AttackGamer attack = g.getProviderTask().getGamerToAttack();
			Gamer underAttack = opponents.get(attack.IDofAttackedGamer);
			underAttack.getsDamage(attack.strength);
		}
	}

	/**
	 * Warten auf die Attacke der Zombies. Sobald diese vorhanden sind, wird der
	 * Schaden den Menschen sofort zugefügt.
	 */
	private void getZombiesAttackResults() {
		getAttackResults(zombies, humans);
	}

	/**
	 * Warten auf die Attacke der Menschen. Sobald diese vorhanden sind, wird
	 * der Schaden den Zombies sofort zugefügt.
	 */
	private void getHumansAttackResults() {
		getAttackResults(humans, zombies);
	}

	/**
	 * Entfernt tote Spieler aus dem Kampf. Ein Spieler ist tot, wenn sein
	 * Gesundheitswert unter oder gleich 0 ist. Der tote Spieler wird der Liste
	 * der toten Spieler hinzugefügt. Alle Angreifer erhalten einen Bonus von 25
	 * Gesundheitspunkten, da sie einen Gegner erfolgreich töten konnten.
	 * 
	 * @param victims
	 *            die Liste mit den potentiell toten Spieler
	 * @param attackers
	 *            die Liste mit den Spielern die eventuell einen
	 *            Gesundheitsbonus erhalten
	 */
	private void removeDeadGamers(HashMap<String, Gamer> victims, HashMap<String, Gamer> attackers) {
		ArrayList<String> newDeadGamers = new ArrayList<String>();
		for (Gamer g : victims.values()) {
			if (g.getHealth() <= 0) {
				String ID = new Integer(g.getGamerID()).toString();
				deadGamers.add(g);
				newDeadGamers.add(ID);
			}
		}
		for (String ID : newDeadGamers) {
			victims.remove(ID);
			giveHealthBonus(attackers);
		}
	}

	/**
	 * Wurde ein Gegner getötet, erhalten alle noch lebenden Angreifer einen
	 * Gesundheitsbonus von 25.
	 * 
	 * @param attackers die Liste mit den angreifenden Spielern
	 */
	private void giveHealthBonus(HashMap<String, Gamer> attackers) {
		for (Gamer g : attackers.values()) {
			g.restoreHealth(25);
		}
	}

	/**
	 * Entferne tote Zombies aus dem Kampf
	 */
	private void removeDeadZombies() {
		removeDeadGamers(zombies, humans);
	}

	/**
	 * Entferne tote Spieler aus dem Kampf
	 */
	private void removeDeadHumans() {
		removeDeadGamers(humans, zombies);
	}

}
