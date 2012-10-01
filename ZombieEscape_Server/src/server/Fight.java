package server;

import java.util.ArrayList;
import java.util.HashMap;

import socket.Socket_AttackGamer;
import socket.Socket_GamerInFight;
import socket.Socket_Utils;

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

	public void addGamer(Gamer gamer) {
		System.out.println("add gamer to fight: " + gamer.getName());
		synchronized (queue) {
			queue.add(gamer);
		}
		gamer.setFight(this);
	}

	@Override
	public void run() {
		System.out.println("started fight");
		
		while (true){
			addQueuedGamersToFight();
			
			// breaks if all zombies or all humans are dead
			if(zombies.isEmpty() || humans.isEmpty()){
				break;
			}
			
			makeARound();
		}
		fightOver();
	}

	private void makeARound() {
		System.out.println("entered round");
		// Order does not matter at the moment
		letZombiesAttack();
		letHumansAttack();

		getZombiesAttackResults();
		getHumansAttackResults();

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
			g.quitGame();
		}
		synchronized (queue) {
			for (Gamer g : queue) {
				g.setFight(null);
			}
		}
	}

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

	private void letAttack(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		ArrayList<Socket_GamerInFight> sock_opponents = Socket_Utils.transformGamerslistToSocket_GamerInFight(opponents
				.values());
		ArrayList<Socket_GamerInFight> sock_allies = Socket_Utils.transformGamerslistToSocket_GamerInFight(attackers.values());
		ArrayList<Socket_GamerInFight> fightingGamers = new ArrayList<Socket_GamerInFight>(sock_allies);
		fightingGamers.addAll(sock_opponents);
		for (Gamer g : attackers.values()) {
			g.getProviderTask().listFightingGamers(fightingGamers);
		}
	}

	private void letZombiesAttack() {
		letAttack(zombies, humans);
	}

	private void letHumansAttack() {
		letAttack(humans, zombies);
	}

	private void getAttackResults(HashMap<String, Gamer> attackers, HashMap<String, Gamer> opponents) {
		for (Gamer g : attackers.values()) {
			Socket_AttackGamer attack = g.getProviderTask().getGamerToAttack();
			Gamer underAttack = opponents.get(attack.IDofAttackedGamer);
			underAttack.getsDamage(attack.strength);
		}
	}

	private void getZombiesAttackResults() {
		getAttackResults(zombies, humans);
	}

	private void getHumansAttackResults() {
		getAttackResults(humans, zombies);
	}

	private void removeDeadGamers(HashMap<String, Gamer> gamers) {
		ArrayList<String> newDeadGamers = new ArrayList<String>();
		for (Gamer g : gamers.values()) {
			if (g.getHealth() <= 0) {
				String ID = new Integer(g.getGamerID()).toString();
				deadGamers.add(g);
				newDeadGamers.add(ID);
			}
		}
		for (String ID : newDeadGamers) {
			gamers.remove(ID);
		}
	}

	private void removeDeadZombies() {
		removeDeadGamers(zombies);
	}

	private void removeDeadHumans() {
		removeDeadGamers(humans);
	}

}
