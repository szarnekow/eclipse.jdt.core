class Foo {
	private int z;
	
	/**
	 * @throws IllegalArgumentException
	 *    | 10000 <=
	 */
	void foo(int x) {}

	/**
	 * @may_throw IllegalArgumentException
	 *    | 5000 <=
	 */
	void bar(int x) {}
}