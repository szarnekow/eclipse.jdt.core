package testpackage;

class Foo {
	
	int[] getElements() { return null; }
	
	/**
	 * @post | Arrays.equals(getElements(), 0, getElements().length, old(getElements()), 0, old(getElements()).length)
	 */
	void foo() {}
	
	/**
	 * @post | getElements().length == old(getElements()).length && java.util.Arrays.equals(getElements(), old(getElements()))
	 */
    void bar() {}
}