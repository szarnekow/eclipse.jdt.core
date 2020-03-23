class Foo {
	
	Object bar() { return null; }
	
	/**
	 * @inspects | this, ...stuff, other,
	 */
	Foo bar1(Foo other, Iterable<Foo> stuff, Foo quux) {}
	
	/**
	 * @mutates | quux, bar(
	 */
	Foo bar2(Foo other, Iterable<Foo> stuff, Foo quux) {}
	
	/**
	 * @mutates_properties | bar)
	 */
	Foo bar3(Foo other, Iterable<Foo> stuff, Foo quux) {}
	
	/**
	 * @creates | result -
	 */
	Foo bar4(Foo other, Iterable<Foo> stuff, Foo quux) {}
}