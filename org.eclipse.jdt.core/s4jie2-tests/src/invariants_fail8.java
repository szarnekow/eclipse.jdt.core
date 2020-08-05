public class invariants_fail8 {
	
	/**
	 * @invar | 10 < x
	 * @invar | getDifference() >= 0
	 */
	int x = 40;
	int y = 30;
	
	public int getDifference() {
		return y - x;
	}
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail8();
	}
}