package org.eclipse.jdt.core;

import org.eclipse.jdt.internal.compiler.batch.Main;

public class s4jie2TestSuite {
	
	public static void assertTrue(boolean b) { if (!b) throw new AssertionError(); }
	public static void assertFalse(boolean b) { assertTrue(!b); }
	
	public static void assertEquals(boolean actual, boolean expected) { assertTrue(actual == expected); }
	
	public static void test(String filename, boolean expectedSuccess) {
		System.out.println("Test " + filename + " start");
		assertEquals(Main.compile("s4jie2-tests/src/" + filename + ".java -d s4jie2-tests/bin"), expectedSuccess);
		System.out.println("Test " + filename + " success");
	}
	
	public static void main(String[] args) {
		test("FormalLine_success", true);
		test("FormalLine_syntax_error", false);
		//test("GameCharacter_pre", true);
		//test("GameCharacter_pre_fail", false);
		
		//test("GameCharacter_pre_post", true);
		//test("GameCharacter_pre_post_syntax_error", false);
		
		System.out.println("s4jie2TestSuite: All tests passed.");
	}

}
