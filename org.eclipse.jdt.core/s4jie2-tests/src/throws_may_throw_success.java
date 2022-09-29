class Main {
	
	/**
	 * @throws IllegalArgumentException | x < 0
	 * @throws IllegalArgumentException | x == 7
	 * may_throw IllegalArgumentException | 5000 <= x
	 */
	public static void foo(int x) {
		if (x < -5)
			throw new IllegalArgumentException();
		if (10000 <= x)
			throw new IllegalArgumentException();
	}
	
	public static void main(String[] args) {
		try {
			foo(-10);
			System.out.println("Did not throw! :-(");
		} catch (IllegalArgumentException e) {
			System.out.println("Caught the IAE");
		}
		
		foo(10);
		
		try {
			foo(20000);
			System.out.println("Did not throw! :-(");
		} catch (IllegalArgumentException e) {
			System.out.println("Caught the IAE");
		}
		
		try {
			foo(-3);
			System.out.println("Did not throw! :-(");
		} catch (IllegalArgumentException e) {
			System.out.println("Caught the IAE");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			foo(7);
			System.out.println("Did not throw! :-(");
		} catch (IllegalArgumentException e) {
			System.out.println("Caught the IAE");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
	}
}