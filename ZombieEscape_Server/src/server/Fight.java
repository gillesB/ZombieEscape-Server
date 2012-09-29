package server;

import java.util.ArrayList;
import java.util.HashMap;

import socket.Socket_AttackGamer;
import socket.Socket_Opponent;
import socket.Socket_Utils;

public class Fight implements Runnable {

	private HashMap<String, Gamer> zombies;
	private HashMap<String, Gamer> humans;
	private ArrayList<Gamer> queue;
	private ArrayList<Gamer> deadGamers;

	public void addGamer(Gamer gamer) {
		synchronized (queue) {
			queue.add(gamer);
		}
		gamer.setFight(this);
	}

	@Override
	public void run() {
		while (!(zombies.isEmpty() || humans.isEmpty() && queue.isEmpty())) {
			// breaks if all zombies or all humans are dead, and there are no
			// queued gamers left
			makeARound();
		}
		fightOver();
	}

	private void makeARound() {
		addQueuedGamerToFight();
		// Order does not matter at the moment
		letZombiesAttack();
		letHumansAttack();

		removeDeadZombies();
		removeDeadHumans();
	}

	private void fightOver() {
		for (Gamer g : zombies.values()) {
			g.setFight(null);
		}
		for (Gamer g : humans.values()) {
			g.setFight(null);
		}
		for (Gamer g : deadGamers) {
			g.setFight(null);
		}
		synchronized (queue) {
			for (Gamer g : queue) {
				g.setFight(null);
			}
		}
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

	private void letAttack(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		ArrayList<Socket_Opponent> sock_opponents = Socket_Utils.transformGamerslistToSocket_OpponentList(opponents
				.values());
		for (Gamer g : attackers.values()) {
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

	private void removeDeadGamers(HashMap<String, Gamer> gamers) {
		for (Gamer g : gamers.values()) {
			if (g.getHealth() <= 0) {
				String ID = new Integer(g.getGamerID()).toString();
				gamers.remove(ID);
				deadGamers.add(g);
			}
		}
	}

	private void removeDeadZombies() {
		removeDeadGamers(zombies);
	}

	private void removeDeadHumans() {
		removeDeadGamers(humans);
	}

}
