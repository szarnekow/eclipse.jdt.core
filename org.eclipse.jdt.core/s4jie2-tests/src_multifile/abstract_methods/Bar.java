class Bar extends Foo implements IFoo {
	
	int x;
	
	int getX() { return x; }
	
	int foo(int dx) {
		int result = x;
		x += dx;
		return result;
	}
	
	public boolean foo(boolean x, byte y) { return false; }
	
	public byte foo(short x, int y) { return (byte)(x + y); }
	
	public short foo(long x, float y) { return (short)(x + y); }
	
	public int foo(char x, double y) { return (int)(x + y); }
	
	public long foo(Object x, long y) { return y; }
	
	public float foo(double x, double y) { return (float)(x + y); }
	
	public double foo(long x, long y) { return x + y; }
	
	public Object foo(int x, int y) { return null; }
	
	public char foo(byte x, byte y) { return (char)(x + y); }
	
}