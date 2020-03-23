import java.util.ArrayList;

class Foo {
	
	Object bar() { return null; }
	
	int getX() { return 0; }
	
	/**
	 * @inspects | this, ...stuff, other
	 * @mutates | quux, bar(), ...stuff
	 * @mutates_properties | bar(), other.getX()
	 */
	Foo bar(Foo other, Iterable<Foo> stuff, Foo quux) {
		return this;
	}
}

class Main {
	public static void main(String[] args) {
		Foo foo = new Foo();
		ArrayList<Foo> foos = new ArrayList<>();
		foos.add(foo);
		foo.bar(foo, foos, foo);
	}
}