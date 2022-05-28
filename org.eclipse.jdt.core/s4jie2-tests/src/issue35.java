class Main {
	
	public int[] xs;
	
	/**
	 * @post | this.xs.length == n
	 */
	Main(int n) {
		this.xs = new int[n];
		
		int i = 0;
		for (int x : xs)
			xs[i++] = x + 1;
	}

	public static void main(String[] args) {
		new Main(5);
	}
	
}