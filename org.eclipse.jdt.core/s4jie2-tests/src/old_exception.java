class Foo {
	
	private int x;
	
	int getX() { return x; }
	
	/**
	 * @post | foo == null || foo.getX() == old(foo.getX()) + 1
	 */
	static void increment(Foo foo) {
		if (foo != null)
			foo.x++;
	}
	
	/**
	 * @throws IllegalArgumentException if {@code foo} is null
	 *    | foo == null
	 * @post | foo.getX() == old(foo.getX()) + 1
	 */
	static void increment2(Foo foo) {
		if (foo == null)
			throw new IllegalArgumentException("foo is null");
		foo.x++;
	}
	
	/**
	 * @post | old(foo.x != 0)
	 */
	static void buggyDocs(Foo foo) {
		
	}
}

class Main {
	
	public static void main(String[] args) {
		Foo.increment(new Foo());
		Foo.increment(null);
		Foo.increment2(new Foo());
		try {
			Foo.increment2(null);
			System.out.println("No exception thrown! :-(");
		} catch (IllegalArgumentException e) {
			System.out.println("Success");
		}
		try {
			Foo.buggyDocs(null);
			System.out.println("No exception thrown! :-(");
		} catch (NullPointerException e) {
			System.out.println("Success");
		}
	}
}