/**
 * @invar | getDifference() >= 0
 */
class invariants_fail10 {
	
	/**
	 * @invar | 10 < x
	 */
	private int x = 40;
	private int y = 30;
	
	int getDifference() { return y - x; }
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail10();
	}
}