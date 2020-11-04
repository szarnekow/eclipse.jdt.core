abstract class Foo {
	
	abstract int getX();
	
	/**
	 * @pre | 0 <= dx
	 * @post | getX() == old(getX()) + dx
	 * @post | result == old(getX())
	 */
	abstract int foo(int dx);
	
}

