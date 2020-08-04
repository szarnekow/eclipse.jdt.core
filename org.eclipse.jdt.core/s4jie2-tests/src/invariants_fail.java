public class invariants_fail {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	private int x = 40;
	private int y = 30;
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail();
	}
}