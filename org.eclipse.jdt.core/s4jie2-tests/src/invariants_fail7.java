public class invariants_fail7 {
	
	/**
	 * @invar | 10 < x
	 * @invar | getDifference() >= 0
	 */
	private int x = 40;
	private int y = 30;
	
	int getDifference() {
		return y - x;
	}
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail7();
	}
}