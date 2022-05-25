class Main {
	
	/**
	 * @invar | 10 <= x
	 */
	private int x;
	
	public int getX() { return x; }
	
	public Main() {
		foo();
	}
	
	/**
	 * @post | 0 +
	 *       | getX()
	 *       | ==
	 *       | old(getX())
	 *       | + 1
	 */
	private void foo() {
		x++;
	}
	
	public static void main(String[] args) {
		new Main();
	}

}