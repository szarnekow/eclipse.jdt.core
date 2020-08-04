public class invariants_fail2 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	private int x = 40;
	private int y = 30;
	
	public int getDifference() {
		return y - x;
	}
	
	public invariants_fail2() {
		
		getDifference();
		
	}
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail2();
	}
}