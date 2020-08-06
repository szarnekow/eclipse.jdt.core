import java.util.function.IntSupplier;

class Foo {
	
	static boolean foo(IntSupplier supplier) { return true; }

	/** @invar | foo(() -> y) */
	private final int y;
	
}
