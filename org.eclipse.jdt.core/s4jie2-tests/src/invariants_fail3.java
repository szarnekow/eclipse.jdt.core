public class invariants_fail3 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	private int x = 30;
	private int y = 40;
	
	public int getDifference() { return y - x; }
	
	/** @inspects | this */
	public void foo() {
	}
	
	/** @mutates | this */
	public void bar1() {
		x += 20;
		foo();
	}
	
	/** @mutates_properties | this.getDifference() */
	public void bar2() {
		x += 20;
		bar1();
	}
	
	/** @mutates_properties | getDifference() */
	public void bar3() {
		x += 20;
		bar2();
	}
	
	/** @mutates_properties | getDifference() */
	public void bar4() {
		x += 20;
		bar3();
	}
	
}

class Main {
	public static void main(String[] args) {
		try {
			new invariants_fail3().bar1();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail3().bar2();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail3().bar3();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail3().bar4();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}