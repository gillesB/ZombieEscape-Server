package server;

import java.util.ArrayList;
import java.util.HashMap;

import socket.Socket_AttackGamer;
import socket.Socket_GamerOverview;
import socket.Socket_Utils;

public class Fight implements Runnable {

	private HashMap<String, Gamer> zombies;
	private HashMap<String, Gamer> humans;
	private ArrayList<Gamer> queue;

	public void addGamer(Gamer gamer) {
		synchronized (queue) {
			queue.add(gamer);
		}
	}

	@Override
	public void run() {
		while (!(zombies.isEmpty() || humans.isEmpty() && queue.isEmpty())) {
			// breaks if all zombies or all humans are dead, and there are no
			// queued gamers left
			makeARound();
		}
	}

	private void makeARound() {
		addQueuedGamerToFight();
		// Order does not matter at the moment
		letZombiesAttack();
		letHumansAttack();

		removeDeadZombies();
		removeDeadHumans();
	}

	private void letAttack(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		ArrayList<Socket_GamerOverview> sock_opponents = Socket_Utils.transformGamerslistToSocket_GamerOverviewList(opponents.values());
		for (Gamer g : zombies.values()) {
			Socket_AttackGamer attack = g.getProviderTask().listOpponents(sock_opponents);
			Gamer underAttack = opponents.get(attack.IDofAttackedGamer);
			underAttack.getsDamage(attack.strength);
		}
	}

	private void letZombiesAttack() {
		letAttack(zombies, humans);
	}

	private void letHumansAttack() {
		letAttack(humans, zombies);
	}

	private void removeDeadZombies() {
		// TODO Auto-generated method stub

	}

	private void removeDeadHumans() {
		// TODO Auto-generated method stub

	}

	private void addQueuedGamerToFight() {
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

}
