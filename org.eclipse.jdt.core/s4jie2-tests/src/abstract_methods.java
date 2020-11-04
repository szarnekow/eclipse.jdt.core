abstract class Foo {
	
	abstract int getX();
	
	/**
	 * @pre | 0 <= dx
	 * @post | getX() == old(getX()) + dx
	 * @post | result == old(getX())
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

class Baz extends Foo {
	
	int x;
	
	int getX() { return x; }
	
	int foo(int dx) {
		int result = x;
		x -= dx;
		return result;
	}
	
}

class Quux extends Foo {
	
	int x;
	
	int getX() { return x; }
	
	int foo(int dx) {
		int result = x;
		x += dx;
		return result - 1;
	}
	
}

class Main {
	
	public static void main(String[] args) {
		((Foo)new Bar()).foo(10);
		
		try {
			((Foo)new Bar()).foo(-5);
			System.out.println("No exception thrown :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			((Foo)new Baz()).foo(10);
			System.out.println("No exception thrown :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		try {
			((Foo)new Quux()).foo(10);
			System.out.println("No exception thrown :-(");
		} catch (AssertionError e) {
			e.printStackTrace();
		}
		
		System.out.println("Success!");
	}
	
}