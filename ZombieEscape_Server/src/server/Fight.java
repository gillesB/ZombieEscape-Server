package server;

import java.util.ArrayList;

import com.sun.org.apache.xml.internal.serializer.utils.Utils;

import socket.Socket_AttackGamer;
import socket.Socket_GamerOverview;
import socket.Socket_Utils;

public class Fight implements Runnable{
	
	ArrayList<Gamer> zombies;
	ArrayList<Gamer> humans;
	ArrayList<Gamer> queue;
	
	void addGamer(Gamer gamer){
		
	}

	@Override
	public void run() {
		while(!(zombies.isEmpty() || humans.isEmpty() && queue.isEmpty())){ //breaks if all zombies or all humans are dead, and there are no queued gamers left
			makeARound();
		}		
	}

	private void makeARound() {
		addQueuedGamerToFight();
		//Order does not matter at the moment
		letZombiesAttack();
		letHumansAttack();
		
		removeDeadZombies();
		removeDeadHumans();
	}

	private void letZombiesAttack() {
		ArrayList<Socket_GamerOverview> opponents = Socket_Utils.transformGamerslistToSocket_GamerOverviewList(humans);
		for(Gamer g : zombies){
			Socket_AttackGamer attack = g.getProviderTask().listOpponents(opponents);
			Gamer underAttack = getGamerWithID(attack.gamerID);
			underAttack.getsDamage(attack.strength);
		}		
	}

	private void letHumansAttack() {
		ArrayList<Socket_GamerOverview> opponents = Socket_Utils.transformGamerslistToSocket_GamerOverviewList(zombies);
		for(Gamer g : humans){
			Socket_AttackGamer attack = g.getProviderTask().listOpponents(opponents);
			Gamer underAttack = getGamerWithID(attack.gamerID);
			underAttack.getsDamage(attack.strength);
		}		
	}

	private Gamer getGamerWithID(Object gamerID) {
		// TODO Auto-generated method stub
		return null;
	}

	private void removeDeadZombies() {
		// TODO Auto-generated method stub
		
	}

	private void removeDeadHumans() {
		// TODO Auto-generated method stub
		
	}

	private void addQueuedGamerToFight() {
		// TODO Auto-generated method stub
		
	}	
	

}
