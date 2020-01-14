class Minimal {
	static Runnable foo(int x) {
		return () -> System.out.println(x);
	}
}
