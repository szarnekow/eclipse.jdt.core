class Foo {
	
	Object bar() { return null; }
	
	Object baz(int x) { return null; }
	
	private Object x;
	
	/**
	 * @inspects | this, ...stuff, other, zazz, x
	 * @mutates | quux, bar(3), ...x, x
	 * @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()
	 */
	Foo bar(Foo other, Iterable<Foo> stuff, Foo quux) {}
}