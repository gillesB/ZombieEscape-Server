package socket;

public class Socket_AttackGamer {

	public String IDofAttackedGamer;
	public int strength;

	public Socket_AttackGamer copy() {
		Socket_AttackGamer copy = new Socket_AttackGamer();
		copy.IDofAttackedGamer = this.IDofAttackedGamer;
		copy.strength = this.strength;
		return copy;
	}

}
