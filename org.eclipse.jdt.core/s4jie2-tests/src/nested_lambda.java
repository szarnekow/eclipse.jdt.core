import java.util.function.IntPredicate;

class Foo {
	
	static boolean bar(IntPredicate predicate) { return true; }
	
	/**
	 * @post | bar(x -> bar(y -> true))
	 * @post | 
	 */
	static void foo() {}
}