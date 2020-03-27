abstract class Foo {
	
	abstract int getX();
	
	/**
	 * @pre | 0 <= dx
	 * @post | result == old(getX()) && getX() == old(getX()) + dx
	 */
	abstract int foo(int dx);
	
}

class Bar extends Foo {
	
	int x;
	
	int getX() { return x; }
	
	int foo(int dx) {
		int result = x;
		x += dx;
		return result;
	}
	
}

class Main {
	
	public static void main(String[] args) {
		new Bar().foo(10);
		System.out.println("Success!");
	}
	
}