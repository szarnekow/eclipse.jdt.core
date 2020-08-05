/**
 * @invar | getDifference() >= 0
 */
public class invariants_fail9 {
	
	/**
	 * @invar | 10 < x
	 */
	private int x = 40;
	private int y = 30;
	
	public int getDifference() { return y - x; }
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail9();
	}
}