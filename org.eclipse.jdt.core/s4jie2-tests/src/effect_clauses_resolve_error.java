class Foo {
	
	Object bar() { return null; }
	
	/**
	 * @inspects | this, ...stuff, other, zazz
	 * @mutates | quux, bar(3)
	 * @mutates_properties | bar(), other
	 */
	Foo bar(Foo other, Iterable<Foo> stuff, Foo quux) {}
}