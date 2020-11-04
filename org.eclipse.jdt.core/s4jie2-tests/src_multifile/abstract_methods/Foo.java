abstract class Foo {
	
	abstract int getX();
	
	/**
	 * @pre | 0 <= dx
	 * @post | getX() == old(getX()) + dx
	 * @post | result == old(getX())
	 */
	abstract int foo(int dx);
	
}

interface IFoo {
	
	/**
	 * @pre | x == true && y >= 5
	 * @post | result == y >= 10
	 */
	boolean foo(boolean x, byte y);
	
	/**
	 * @pre | x == 7 && y >= 9
	 * @post | result == 16 && foo(true, (byte)5) == old(foo(true, (byte)5))
	 */
	byte foo(short x, int y);
	
	/**
	 * @pre | x == 7 && y >= 9.0f
	 * @post | result == 16 && foo(true, (byte)5) == old(foo(true, (byte)5))
	 */
	short foo(long x, float y);
	
	/**
	 * @pre | x == 7 && y >= 9.0
	 * @post | result == 16 && foo(true, (byte)5) == old(foo(true, (byte)5))
	 */
	int foo(char x, double y);
	
	/**
	 * @pre | x == null && y >= 9
	 * @post | result == 9
	 */
	long foo(Object x, long y);
	
	/**
	 * @pre | x == 7 && y >= 9
	 * @post | result == 16
	 */
	float foo(double x, double y);
	
	/**
	 * @pre | x == 7 && y >= 9
	 * @post | result == 16
	 */
	double foo(long x, long y);
	
	/**
	 * @pre | x == 7 && y >= 9
	 * @post | result == null
	 */
	Object foo(int x, int y);
	
	/**
	 * @pre | x == 7 && y >= 9
	 * @post | result == 16
	 */
	char foo(byte x, byte y);
	
}