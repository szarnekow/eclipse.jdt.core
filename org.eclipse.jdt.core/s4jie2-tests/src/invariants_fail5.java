public class invariants_fail5 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	int x = 40;
	int y = 30;
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail5();
	}
}