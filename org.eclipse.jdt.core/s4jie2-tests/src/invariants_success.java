/**
 * @invar | 20 < getX()
 */
public class invariants_success {
	
	/**
	 * @invar | 10 < x
	 */
	private int x = 30;
	
	/**
	 * @invar | 20 < getXInternal()
	 * @post | 10 < result
	 */
	int getXInternal() { return x; }
	
	public int getX() { return x; }
	
}

class Main {
	public static void main(String[] args) {
		invariants_success s = new invariants_success();
		s.getXInternal();
 		s.getX();
	}
}