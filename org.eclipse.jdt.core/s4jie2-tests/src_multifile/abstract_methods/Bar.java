class Bar extends Foo {
	
	int x;
	
	int getX() { return x; }
	
	int foo(int dx) {
		int result = x;
		x += dx;
		return result;
	}
	
}

