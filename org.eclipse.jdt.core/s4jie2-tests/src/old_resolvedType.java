class old_resolvedtype {
	
	static <T> boolean foo(T args) { return true; }
	
	/** @post | foo(old("Hello")) */
	static void bar() {}
}