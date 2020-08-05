public class invariants_fail12 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	private int x = 30;
	private int y = 40;
	
	public invariants_fail12(boolean fail) {
		if (fail) {
			x += 20;
			return;
		}
	}
	
	/** @mutates | this */
	public boolean foo() {
		x += 20;
		if (x > 35)
			return true;
		else
			return false;
	}
	
}

class Main {
	public static void main(String[] args) {
		try {
			new invariants_fail12(true);
			System.out.println("No exception thrown :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail12(false).foo();
			System.out.println("No exception thrown :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}