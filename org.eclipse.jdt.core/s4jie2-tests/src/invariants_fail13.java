public class invariants_fail13 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	private int x = 30;
	private int y = 40;
	
	public void foo() {
		x += 20;
	}
	
}

class Main {
	public static void main(String[] args) {
		new invariants_fail13().foo();
	}
}