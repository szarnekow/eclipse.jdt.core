class Minimal {
	static java.util.function.IntSupplier foo(int x) {
		return () -> x;
	}
}
