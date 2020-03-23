class Foo {
	
	Object bar() { return null; }
	
	private Object x;
	
	/**
	 * @inspects | this, ...stuff, other, zazz, x
	 * @mutates | quux, bar(3), x
	 * @mutates_properties | bar(), other, x
	 */
	Foo bar(Foo other, Iterable<Foo> stuff, Foo quux) {}
}