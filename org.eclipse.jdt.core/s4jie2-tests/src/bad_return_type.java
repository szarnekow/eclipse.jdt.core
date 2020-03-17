class Foo<A, B> {}

class bad_return_type {
	
	/** @post | result != null */
	static Foo<Object> foo() {}
	
}