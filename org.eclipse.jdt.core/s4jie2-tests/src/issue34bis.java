class Main {
	
	/**
	 * @invar | x != 0
	 */
	private int x;
	
	public int getX() { return x; }
	
	public Main() {
		foo();
	}
	
	/**
	 * @post | 1 +
	 *       | old(getX()) 
	 *       | ==
	 *       | getX()
	 *       | + 0
	 */
	private void foo() {
		x++;
	}
	
	public static void main(String[] args) {
		new Main();
	}

}