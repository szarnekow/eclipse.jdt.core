public class invariants_fail4 {
	
	/**
	 * @invar | 10 < x
	 * @invar | x < y
	 */
	int x = 30;
	int y = 40;
	
	int getDifferenceInternal() { return y - x; }
	
	public int getDifference() { return getDifferenceInternal(); }
	
	/** @inspects | this */
	void fooInternal() {}
	
	/** @inspects | this */
	public void foo() {
		x += 20;
		getDifferenceInternal();
		getDifference();
	}
	
	/** @mutates | this */
	public void bar1() {
		x += 20;
		fooInternal();
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
			new invariants_fail4().foo();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail4().bar1();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail4().bar2();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail4().bar3();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			new invariants_fail4().bar4();
			System.err.println("No exception! :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}