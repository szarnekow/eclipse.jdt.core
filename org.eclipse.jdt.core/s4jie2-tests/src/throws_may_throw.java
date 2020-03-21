class Foo {
	private int z;
	
	/**
	 * @pre | 0 <= x
	 * @throws IllegalArgumentException
	 *    | 10000 <= x
	 * @throws IllegalArgumentException
	 *    | 10000 <= y
	 * @throws IllegalArgumentException
	 *    | 10000 <= z
	 * @may_throw IllegalArgumentException
	 *    | 5000 <= x
	 * @may_throw IllegalArgumentException
	 *    | 5000 <= y
	 * @may_throw IllegalArgumentException
	 *    | 5000 <= z 
	 * @post | true
	 */
	void baz(int x) {} 
}