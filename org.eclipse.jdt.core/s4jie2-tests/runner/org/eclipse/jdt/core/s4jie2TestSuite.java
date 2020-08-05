package org.eclipse.jdt.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

import org.eclipse.jdt.internal.compiler.batch.Main;

/**
 * @since 3.24
 */
@SuppressWarnings("nls")
public class s4jie2TestSuite {

	public static void deleteFileTree(String path) throws IOException {
	     Files.walkFileTree(FileSystems.getDefault().getPath(path), new SimpleFileVisitor<Path>() {
	         @Override
	         public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
	             throws IOException
	         {
	             Files.delete(file);
	             return FileVisitResult.CONTINUE;
	         }
	         @Override
	         public FileVisitResult postVisitDirectory(Path dir, IOException e)
	             throws IOException
	         {
	             if (e == null) {
	                 Files.delete(dir);
	                 return FileVisitResult.CONTINUE;
	             } else {
	                 // directory iteration failed
	                 throw e;
	             }
	         }
	     });
	}

	public static void assertTrue(boolean b, String message) {
		if (!b) {
			System.err.println("FAIL " + message);
			System.exit(1);
		}
	}

	public static void assertEquals(boolean actual, boolean expected, String message) {
		if (actual != expected) {
			System.err.println("FAIL " + message + ": expected: " + expected + "; actual: " + actual);
			System.exit(1);
		}
	}
	public static String normalize(String text) { return text.replace("\r\n", "\n"); }
	public static void assertEquals(String actual, String expected, String msg) {
		if (!normalize(actual).equals(normalize(expected))) {
			System.err.println("FAIL " + msg + " is not as expected");
			System.err.println("=== expected START ===");
			System.err.println(expected);
			System.err.println("=== expected END ===");
			System.err.println("=== actual START ===");
			System.err.println(actual);
			System.err.println("=== actual END ===");
			System.exit(1);
		}
	}

	private static final String binPath = "s4jie2-tests/bin";
	private static final String multifileBinPath = "s4jie2-tests/bin_multifile";
	private static final String junitPath = System.getenv("JUNIT_PATH");
	private static final String junitPlatformConsoleStandalonePath = junitPath;
	private static final String pathSeparator = System.getProperty("path.separator");

	public static void testCompile(String filename, boolean expectedSuccess, String outExpected, String errExpected) {
		testCompile(false, filename, expectedSuccess, outExpected, errExpected);
	}

	@SuppressWarnings("deprecation")
	public static void testCompile(boolean asModule, String filename, boolean expectedSuccess, String outExpected, String errExpected) {
		System.out.println("     Test " + filename + " start");
		StringWriter outWriter = new StringWriter();
		StringWriter errWriter = new StringWriter();
		String path = "s4jie2-tests/src/" + filename + ".java";
		String fullPath = new File(path).getAbsolutePath();
		String moduleArgs = asModule ? "--module-source-path s4jie2-tests/src s4jie2-tests/src/module-info.java" : "";
		String args = "-source 10 -proc:none " + moduleArgs + " " + path + " -g -d " + binPath + "/" + filename;
		if (Main.compile(args, new PrintWriter(outWriter), new PrintWriter(errWriter)) != expectedSuccess) {
			System.err.println("FAIL compiler success: expected: " + expectedSuccess + "; actual: " + !expectedSuccess);
			System.err.println("=== standard output start ===");
			System.err.println(outWriter.toString());
			System.err.println("=== standard output end ===");
			System.err.println("=== standard error start ===");
			System.err.println(errWriter.toString());
			System.err.println("=== standard error end ===");
			System.exit(1);
		}
		assertEquals(outWriter.toString().replace(fullPath, "SOURCE_FILE_FULL_PATH"), outExpected, "standard output");
		assertEquals(errWriter.toString().replace(fullPath, "SOURCE_FILE_FULL_PATH"), errExpected, "standard error");
		System.out.println("PASS Test " + filename + " compile success");
	}

	@SuppressWarnings("deprecation")
	public static void testCompileMultifile(String rootDirectory, boolean expectedSuccess, String outExpected, String errExpected) {
		System.out.println("     Multifile test " + rootDirectory + " start");
		StringWriter outWriter = new StringWriter();
		StringWriter errWriter = new StringWriter();
		String path = "s4jie2-tests/src_multifile/" + rootDirectory;
		String fullPath = new File(path).getAbsolutePath();
		ArrayList<String> classPath = new ArrayList<>();
		classPath.add(junitPath);
		if (!rootDirectory.equals("logicalcollections"))
			classPath.add(multifileBinPath + "/logicalcollections");
		String classPathString = String.join(pathSeparator, classPath);
		String args = "-11 -cp " + classPathString + " -proc:none " + path + " -g -d " + multifileBinPath + "/" + rootDirectory;
		if (Main.compile(args, new PrintWriter(outWriter), new PrintWriter(errWriter)) != expectedSuccess) {
			System.err.println("FAIL compiler success: expected: " + expectedSuccess + "; actual: " + !expectedSuccess);
			System.err.println("=== standard output start ===");
			System.err.println(outWriter.toString().replace(fullPath, "SOURCE_ROOT_PATH"));
			System.err.println("=== standard output end ===");
			System.err.println("=== standard error start ===");
			System.err.println(errWriter.toString().replace(fullPath, "SOURCE_ROOT_PATH"));
			System.err.println("=== standard error end ===");
			System.exit(1);
		}
		assertEquals(outWriter.toString().replace(fullPath, "SOURCE_ROOT_PATH"), outExpected, "standard output");
		assertEquals(errWriter.toString().replace(fullPath, "SOURCE_ROOT_PATH"), errExpected, "standard error");
		System.out.println("PASS Multifile test " + rootDirectory + " compile success");
	}

	public static void readFullyInto(InputStream stream, StringBuilder builder) {
		InputStreamReader reader = new InputStreamReader(stream);
		char[] buffer = new char[65536];
		try {
			for (;;) {
				int result = reader.read(buffer);
				if (result < 0) break;
				builder.append(buffer, 0, result);
			}
		} catch (IOException e) {
			e.printStackTrace();
			builder.append("<exception while reading from subprocess>");
		}
	}

	public static void testCompileAndRun(boolean enableAssertions, String filename, boolean expectedSuccess, String outExpected, String errExpected) throws IOException {
		testCompile(filename, true, "", "");

		String classpath = binPath+"/"+filename;
		Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-classpath", classpath, enableAssertions ? "-ea" : "-da", "Main").start();
		StringBuilder stdoutBuffer = new StringBuilder();
		Thread stdoutThread = new Thread(() -> readFullyInto(process.getInputStream(), stdoutBuffer));
		stdoutThread.start();
		StringBuilder stderrBuffer = new StringBuilder();
		Thread stderrThread = new Thread(() -> readFullyInto(process.getErrorStream(), stderrBuffer));
		stderrThread.start();
		int exitCode;
		try {
			exitCode = process.waitFor();
			stdoutThread.join();
			stderrThread.join();
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		String stdout = stdoutBuffer.toString();
		String stderr = stderrBuffer.toString();

		if ((exitCode == 0) != expectedSuccess) {
			System.err.println("FAIL execution success: expected: " + expectedSuccess + "; actual: exit code " + exitCode);
			System.err.println("=== standard output start ===");
			System.err.println(stdout);
			System.err.println("=== standard output end ===");
			System.err.println("=== standard error start ===");
			System.err.println(stderr);
			System.err.println("=== standard error end ===");
			System.exit(1);
		}
		assertEquals(stdout, outExpected, "standard output");
		assertEquals(stderr, errExpected, "standard error");
		System.out.println("PASS Test "+ filename + " execution success");
	}

	public static void testCompileAndRunMultifile(String rootDirectory, boolean expectedSuccess, String outExpected, String errExpected) throws IOException {
		testCompileMultifile(rootDirectory, true, "", "");

		String classpath =
				//junitPath + pathSeparator +
				junitPlatformConsoleStandalonePath + pathSeparator +
				multifileBinPath + "/logicalcollections" + pathSeparator +
				multifileBinPath + "/" + rootDirectory;
		Process process = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-classpath", classpath, "-ea", "org.junit.platform.console.ConsoleLauncher", "--fail-if-no-tests", "--disable-banner", "-details-theme=ascii", "--disable-ansi-colors", "--include-classname=.*", "--scan-classpath=" + multifileBinPath + "/" + rootDirectory).start();
		StringBuilder stdoutBuffer = new StringBuilder();
		Thread stdoutThread = new Thread(() -> readFullyInto(process.getInputStream(), stdoutBuffer));
		stdoutThread.start();
		StringBuilder stderrBuffer = new StringBuilder();
		Thread stderrThread = new Thread(() -> readFullyInto(process.getErrorStream(), stderrBuffer));
		stderrThread.start();
		int exitCode;
		try {
			exitCode = process.waitFor();
			stdoutThread.join();
			stderrThread.join();
		} catch (InterruptedException e) {
			throw new AssertionError(e);
		}
		String stdout = stdoutBuffer.toString().replace("Thanks for using JUnit! Support its development at https://junit.org/sponsoring\n\n", "").trim().replaceFirst("Test run finished after [0-9]+ ms", "Test run finished after XX ms");
		String stderr = stderrBuffer.toString();

		if ((exitCode == 0) != expectedSuccess) {
			System.err.println("FAIL execution success: expected: " + expectedSuccess + "; actual: exit code " + exitCode);
			System.err.println("=== standard output start ===");
			System.err.println(stdout);
			System.err.println("=== standard output end ===");
			System.err.println("=== standard error start ===");
			System.err.println(stderr);
			System.err.println("=== standard error end ===");
			System.exit(1);
		}
		assertEquals(stdout, outExpected, "standard output");
		assertEquals(stderr, errExpected, "standard error");
		System.out.println("PASS Multifile test "+ rootDirectory + " execution success");
	}

	public static void main(String[] args) throws IOException {
		if (new File(binPath).exists())
			deleteFileTree(binPath);
		if (new File(multifileBinPath).exists())
			deleteFileTree(multifileBinPath);

		testCompile("Minimal", true, "", "");

		testCompileAndRun(false, "GameCharacter_pre", true, "",
				"No exception was thrown! :-(\n" +
				"No exception was thrown! :-(\n");
		testCompileAndRun(true, "GameCharacter_pre", true, "",
				"java.lang.AssertionError: Precondition does not hold\n" +
				"	at GameCharacter.takeDamage(GameCharacter_pre.java:20)\n" +
				"	at Main.main(GameCharacter_pre.java:36)\n" +
				"java.lang.AssertionError: Precondition does not hold\n" +
				"	at GameCharacter.<init>(GameCharacter_pre.java:9)\n" +
				"	at Main.main(GameCharacter_pre.java:44)\n");
		testCompile("GameCharacter_pre_fail", false, "",
				"----------\n" +
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
				"	*    | 0 <=\n" +
				"	         ^^\n" +
				"Syntax error on token \"<=\", Expression expected after this token\n" +
				"----------\n" +
				"1 problem (1 error)\n");
		testCompile("GameCharacter_pre_type_error", false, "",
				"----------\n" +
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 19)\n" +
				"	*    | amount\n" +
				"	       ^^^^^^\n" +
				"Type mismatch: cannot convert from int to boolean\n" +
				"----------\n" +
				"2. ERROR in SOURCE_FILE_FULL_PATH (at line 20)\n" +
				"	* @pre | new Foo().isOk()\n" +
				"	         ^^^^^^^^^\n" +
				"The constructor Foo() is not visible\n" +
				"----------\n" +
				"3. ERROR in SOURCE_FILE_FULL_PATH (at line 20)\n" +
				"	* @pre | new Foo().isOk()\n" +
				"	             ^^^\n" +
				"The type Foo is not visible\n" +
				"----------\n" +
				"4. ERROR in SOURCE_FILE_FULL_PATH (at line 20)\n" +
				"	* @pre | new Foo().isOk()\n" +
				"	                   ^^^^\n" +
				"The method isOk() from the type Foo is not visible\n" +
				"----------\n" +
				"5. ERROR in SOURCE_FILE_FULL_PATH (at line 21)\n" +
				"	* @pre | health == 0\n" +
				"	         ^^^^^^\n" +
				"The field GameCharacter_pre_type_error.health is not visible\n" +
				"----------\n" +
				"6. ERROR in SOURCE_FILE_FULL_PATH (at line 22)\n" +
				"	* @pre | this.health == 0\n" +
				"	         ^^^^^^^^^^^\n" +
				"The field GameCharacter_pre_type_error.health is not visible\n" +
				"----------\n" +
				"7. ERROR in SOURCE_FILE_FULL_PATH (at line 23)\n" +
				"	* @pre | helper()\n" +
				"	         ^^^^^^\n" +
				"The method helper() from the type GameCharacter_pre_type_error is not visible\n" +
				"----------\n" +
				"8. ERROR in SOURCE_FILE_FULL_PATH (at line 24)\n" +
				"	* @pre | Foo.class.getName() == \"Foo\"\n" +
				"	         ^^^\n" +
				"The type Foo is not visible\n" +
				"----------\n" +
				"9. ERROR in SOURCE_FILE_FULL_PATH (at line 25)\n" +
				"	* @pre | (bazz += 1) + (bazz = 1) + (bazz++) == 42\n" +
				"	         ^^^^^^^^^^^\n" +
				"Assignments are not allowed inside Javadoc comments\n" +
				"----------\n" +
				"10. ERROR in SOURCE_FILE_FULL_PATH (at line 25)\n" +
				"	* @pre | (bazz += 1) + (bazz = 1) + (bazz++) == 42\n" +
				"	                       ^^^^^^^^^^\n" +
				"Assignments are not allowed inside Javadoc comments\n" +
				"----------\n" +
				"11. ERROR in SOURCE_FILE_FULL_PATH (at line 25)\n" +
				"	* @pre | (bazz += 1) + (bazz = 1) + (bazz++) == 42\n" +
				"	                                    ^^^^^^^^\n" +
				"Assignments are not allowed inside Javadoc comments\n" +
				"----------\n" +
				"11 problems (11 errors)\n");
		testCompile("GameCharacter_pre_post_syntax_error", false, "",
				"----------\n" +
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
				"	*    | 0 <= amount +\n" +
				"	                   ^\n" +
				"Syntax error on token \"+\", ++ expected\n" +
				"----------\n" +
				"2. ERROR in SOURCE_FILE_FULL_PATH (at line 23)\n" +
				"	*    | getHealth() == old(getHealth()) - amount +\n" +
				"	                                                ^\n" +
				"Syntax error on token \"+\", ++ expected\n" +
				"----------\n" +
				"3. ERROR in SOURCE_FILE_FULL_PATH (at line 29)\n" +
				"	/** @post | result == (getHealth() ** 3 > 0) */\n" +
				"	                                    ^\n" +
				"Syntax error on token \"*\", delete this token\n" +
				"----------\n" +
				"4. ERROR in SOURCE_FILE_FULL_PATH (at line 32)\n" +
				"	/** @post | nested.comment == /* */\n" +
				"	                              ^^\n" +
				"Syntax error on tokens, Expression expected instead\n" +
				"----------\n" +
				"4 problems (4 errors)\n");
	    testCompile("GameCharacter_pre_post_type_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 9)\n" +
	    		"	*     | getHealth()\n" +
	    		"	        ^^^^^^^^^^^\n" +
	    		"Type mismatch: cannot convert from int to boolean\n" +
	    		"----------\n" +
	    		"1 problem (1 error)\n");
		testCompileAndRun(true, "GameCharacter_pre_post", true, "",
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.takeDamage$post(GameCharacter_pre_post.java:28)\n" +
				"	at GameCharacter.takeDamage(GameCharacter_pre_post.java:36)\n" +
				"	at Main.main(GameCharacter_pre_post.java:98)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.setHealth$post(GameCharacter_pre_post.java:13)\n" +
				"	at GameCharacter.setHealth(GameCharacter_pre_post.java:17)\n" +
				"	at Main.main(GameCharacter_pre_post.java:105)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.simpleReturnTest$post(GameCharacter_pre_post.java:54)\n" +
				"	at GameCharacter.simpleReturnTest(GameCharacter_pre_post.java:58)\n" +
				"	at Main.main(GameCharacter_pre_post.java:112)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.returnInsideIfTest$post(GameCharacter_pre_post.java:62)\n" +
				"	at GameCharacter.returnInsideIfTest(GameCharacter_pre_post.java:67)\n" +
				"	at Main.main(GameCharacter_pre_post.java:119)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.returnInsideIfTest$post(GameCharacter_pre_post.java:62)\n" +
				"	at GameCharacter.returnInsideIfTest(GameCharacter_pre_post.java:70)\n" +
				"	at Main.main(GameCharacter_pre_post.java:126)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.booleanResult$post(GameCharacter_pre_post.java:211)\n" +
				"	at Main.booleanResult(GameCharacter_pre_post.java:213)\n" +
				"	at Main.main(GameCharacter_pre_post.java:146)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.byteResult$post(GameCharacter_pre_post.java:216)\n" +
				"	at Main.byteResult(GameCharacter_pre_post.java:218)\n" +
				"	at Main.main(GameCharacter_pre_post.java:153)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.charResult$post(GameCharacter_pre_post.java:221)\n" +
				"	at Main.charResult(GameCharacter_pre_post.java:223)\n" +
				"	at Main.main(GameCharacter_pre_post.java:160)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.doubleResult$post(GameCharacter_pre_post.java:226)\n" +
				"	at Main.doubleResult(GameCharacter_pre_post.java:228)\n" +
				"	at Main.main(GameCharacter_pre_post.java:167)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.floatResult$post(GameCharacter_pre_post.java:231)\n" +
				"	at Main.floatResult(GameCharacter_pre_post.java:233)\n" +
				"	at Main.main(GameCharacter_pre_post.java:174)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.intResult$post(GameCharacter_pre_post.java:236)\n" +
				"	at Main.intResult(GameCharacter_pre_post.java:238)\n" +
				"	at Main.main(GameCharacter_pre_post.java:181)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.longResult$post(GameCharacter_pre_post.java:241)\n" +
				"	at Main.longResult(GameCharacter_pre_post.java:243)\n" +
				"	at Main.main(GameCharacter_pre_post.java:188)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.shortResult$post(GameCharacter_pre_post.java:246)\n" +
				"	at Main.shortResult(GameCharacter_pre_post.java:248)\n" +
				"	at Main.main(GameCharacter_pre_post.java:195)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.genericResult$post(GameCharacter_pre_post.java:251)\n" +
				"	at Main.genericResult(GameCharacter_pre_post.java:259)\n" +
				"	at Main.main(GameCharacter_pre_post.java:202)\n");
		testCompileAndRun(true, "GameCharacter_ctor_post", true, "",
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.GameCharacter$post(GameCharacter_ctor_post.java:21)\n" +
				"	at GameCharacter.<init>(GameCharacter_ctor_post.java:36)\n" +
				"	at Main.main(GameCharacter_ctor_post.java:51)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.GameCharacter$post(GameCharacter_ctor_post.java:21)\n" +
				"	at GameCharacter.<init>(GameCharacter_ctor_post.java:31)\n" +
				"	at Main.main(GameCharacter_ctor_post.java:59)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.GameCharacter$post(GameCharacter_ctor_post.java:23)\n" +
				"	at GameCharacter.<init>(GameCharacter_ctor_post.java:36)\n" +
				"	at Main.main(GameCharacter_ctor_post.java:67)\n" +
				"java.lang.AssertionError: Postcondition does not hold\n" +
				"	at GameCharacter.GameCharacter$post(GameCharacter_ctor_post.java:25)\n" +
				"	at GameCharacter.<init>(GameCharacter_ctor_post.java:36)\n" +
				"	at Main.main(GameCharacter_ctor_post.java:75)\n");
		testCompile(true, "testpackage/unresolved_type", false, "",
				"----------\n" +
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 8)\n" +
				"	* @post | Arrays.equals(getElements(), 0, getElements().length, old(getElements()), 0, old(getElements()).length)\n" +
				"	          ^^^^^^\n" +
				"Arrays cannot be resolved\n" +
				"----------\n" +
				"1 problem (1 error)\n");
		testCompileAndRun(true, "multiline_lambdas", false, "",
				"Exception in thread \"main\" java.lang.AssertionError: Postcondition does not hold\n" +
				"	at Main.main$post(multiline_lambdas.java:4)\n" +
				"	at Main.main(multiline_lambdas.java:8)\n");
		testCompile("old_resolvedType", true, "", "");
		testCompile("bad_return_type", false, "",
				"----------\n" +
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 6)\n" +
				"	static Foo<Object> foo() {}\n" +
				"	       ^^^\n" +
				"Incorrect number of arguments for type Foo<A,B>; it cannot be parameterized with arguments <Object>\n" +
				"----------\n" +
				"1 problem (1 error)\n");
	    testCompile("qualified_name_visibility_check", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 6)\n" +
	    		"	* @pre | bar.x != 0\n" +
	    		"	         ^^^^^\n" +
	    		"The field Foo.x is not visible\n" +
	    		"----------\n" +
	    		"1 problem (1 error)\n");
	    testCompile("bad_call", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 4)\n" +
	    		"	* @pre | xs.baz()\n" +
	    		"	         ^^^^^^^^\n" +
	    		"Cannot invoke baz() on the array type int[]\n" +
	    		"----------\n" +
	    		"1 problem (1 error)\n");
	    testCompile("nested_lambda", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 9)\n" +
	    		"	* @post | \n" +
	    		"	  ^^^^^\n" +
	    		"Expression expected in formal part\n" +
	    		"----------\n" +
	    		"1 problem (1 error)\n");
	    testCompile("throws_may_throw_syntax_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 6)\n" +
	    		"	*    | 10000 <=\n" +
	    		"	             ^^\n" +
	    		"Syntax error on token \"<=\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	*    | 5000 <=\n" +
	    		"	            ^^\n" +
	    		"Syntax error on token \"<=\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"2 problems (2 errors)\n");
	    testCompile("throws_may_throw_resolve_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 8)\n" +
	    		"	* @throws IllegalArgumentException | 10000 <= y\n" +
	    		"	                                              ^\n" +
	    		"y cannot be resolved to a variable\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
	    		"	*    | 10000 <= z\n" +
	    		"	                ^\n" +
	    		"The field Foo.z is not visible\n" +
	    		"----------\n" +
	    		"3. ERROR in SOURCE_FILE_FULL_PATH (at line 14)\n" +
	    		"	*    | 5000 <= y\n" +
	    		"	               ^\n" +
	    		"y cannot be resolved to a variable\n" +
	    		"----------\n" +
	    		"4. ERROR in SOURCE_FILE_FULL_PATH (at line 15)\n" +
	    		"	* @may_throw IllegalArgumentException | 5000 <= z \n" +
	    		"	                                                ^\n" +
	    		"The field Foo.z is not visible\n" +
	    		"----------\n" +
	    		"4 problems (4 errors)\n");
	    testCompileAndRun(true, "throws_may_throw_success", true,
	    		"Caught the IAE\n" +
	    		"Caught the IAE\n", "");
	    testCompile("invariants_syntax_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 2)\n" +
	    		"	* @invar | 10 <\n" +
	    		"	              ^\n" +
	    		"Syntax error on token \"<\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 7)\n" +
	    		"	* @invar | 10 <\n" +
	    		"	              ^\n" +
	    		"Syntax error on token \"<\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"3. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @invar | 10 <\n" +
	    		"	              ^\n" +
	    		"Syntax error on token \"<\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"3 problems (3 errors)\n");
	    testCompile("invariants_resolve_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 2)\n" +
	    		"	* @invar | 10 < true\n" +
	    		"	           ^^^^^^^^^\n" +
	    		"The operator < is undefined for the argument type(s) int, boolean\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 3)\n" +
	    		"	* @invar | 10 < y\n" +
	    		"	                ^\n" +
	    		"y cannot be resolved to a variable\n" +
	    		"----------\n" +
	    		"3. ERROR in SOURCE_FILE_FULL_PATH (at line 4)\n" +
	    		"	* @invar | 10 < x\n" +
	    		"	                ^\n" +
	    		"The field invariants_resolve_error.x is not visible\n" +
	    		"----------\n" +
	    		"4. ERROR in SOURCE_FILE_FULL_PATH (at line 5)\n" +
	    		"	* @invar | 10 < getX()\n" +
	    		"	                ^^^^\n" +
	    		"The method getX() from the type invariants_resolve_error is not visible\n" +
	    		"----------\n" +
	    		"5. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
	    		"	* @invar | 10 < true\n" +
	    		"	           ^^^^^^^^^\n" +
	    		"The operator < is undefined for the argument type(s) int, boolean\n" +
	    		"----------\n" +
	    		"6. ERROR in SOURCE_FILE_FULL_PATH (at line 11)\n" +
	    		"	* @invar | 10 < y\n" +
	    		"	                ^\n" +
	    		"y cannot be resolved to a variable\n" +
	    		"----------\n" +
	    		"7. ERROR in SOURCE_FILE_FULL_PATH (at line 16)\n" +
	    		"	* @invar | 10 < true\n" +
	    		"	           ^^^^^^^^^\n" +
	    		"The operator < is undefined for the argument type(s) int, boolean\n" +
	    		"----------\n" +
	    		"8. ERROR in SOURCE_FILE_FULL_PATH (at line 17)\n" +
	    		"	* @invar | 10 < x\n" +
	    		"	                ^\n" +
	    		"The field invariants_resolve_error.x is not visible\n" +
	    		"----------\n" +
	    		"8 problems (8 errors)\n");
	    testCompileAndRun(true, "invariants_success", true, "", "");
	    testCompile("effect_clauses_syntax_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 6)\n" +
	    		"	* @inspects | this, ...stuff, other,\n" +
	    		"	                                   ^\n" +
	    		"Syntax error on token \",\", Expression expected after this token\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 11)\n" +
	    		"	* @mutates | quux, bar(\n" +
	    		"	                      ^\n" +
	    		"Syntax error, insert \")\" to complete Expression\n" +
	    		"----------\n" +
	    		"3. ERROR in SOURCE_FILE_FULL_PATH (at line 16)\n" +
	    		"	* @mutates_properties | bar)\n" +
	    		"	                           ^\n" +
	    		"Syntax error on token \")\", delete this token\n" +
	    		"----------\n" +
	    		"4. ERROR in SOURCE_FILE_FULL_PATH (at line 21)\n" +
	    		"	* @creates | result -\n" +
	    		"	                    ^\n" +
	    		"Syntax error on token \"-\", -- expected\n" +
	    		"----------\n" +
	    		"4 problems (4 errors)\n");
	    testCompile("effect_clauses_resolve_error", false, "",
	    		"----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
	    		"	* @inspects | this, ...stuff, other, zazz, x\n" +
	    		"	                                     ^^^^\n" +
	    		"zazz cannot be resolved to a variable\n" +
	    		"----------\n" +
	    		"2. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" +
	    		"	* @inspects | this, ...stuff, other, zazz, x\n" +
	    		"	                                           ^\n" +
	    		"The field Foo.x is not visible\n" +
	    		"----------\n" +
	    		"3. ERROR in SOURCE_FILE_FULL_PATH (at line 11)\n" +
	    		"	* @mutates | quux, bar(3), ...x, x\n" +
	    		"	                   ^^^\n" +
	    		"The method bar() in the type Foo is not applicable for the arguments (int)\n" +
	    		"----------\n" +
	    		"4. ERROR in SOURCE_FILE_FULL_PATH (at line 11)\n" +
	    		"	* @mutates | quux, bar(3), ...x, x\n" +
	    		"	                              ^\n" +
	    		"The field Foo.x is not visible\n" +
	    		"----------\n" +
	    		"5. ERROR in SOURCE_FILE_FULL_PATH (at line 11)\n" +
	    		"	* @mutates | quux, bar(3), ...x, x\n" +
	    		"	                                 ^\n" +
	    		"The field Foo.x is not visible\n" +
	    		"----------\n" +
	    		"6. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                               ^^^^^^\n" +
	    		"Method calls with arguments are not supported here\n" +
	    		"----------\n" +
	    		"7. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                       ^^^^^\n" +
	    		"Method call expected\n" +
	    		"----------\n" +
	    		"8. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                                  ^\n" +
	    		"Can only iterate over an array or an instance of java.lang.Iterable\n" +
	    		"----------\n" +
	    		"9. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                                  ^\n" +
	    		"The field Foo.x is not visible\n" +
	    		"----------\n" +
	    		"10. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                                     ^^^\n" +
	    		"The method bar() is undefined for the type Object\n" +
	    		"----------\n" +
	    		"11. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                                            ^\n" +
	    		"Method call expected\n" +
	    		"----------\n" +
	    		"12. ERROR in SOURCE_FILE_FULL_PATH (at line 12)\n" +
	    		"	* @mutates_properties | bar(), baz(3), other, (...x).bar(), x, (...stuff).quux()\n" +
	    		"	                                                                          ^^^^\n" +
	    		"The method quux() is undefined for the type Foo\n" +
	    		"----------\n" +
	    		"12 problems (12 errors)\n");
	    testCompileAndRun(true, "effect_clauses_success", true, "", "");
	    testCompileAndRun(true, "abstract_methods", true, "Success!\n", "");
	    testCompileAndRun(true, "old_exception", true, "Success\nSuccess\n", "");
	    testCompile("issue16", false, "", "----------\n" +
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 2)\n" +
	    		"	* @invar | ( */\n" +
	    		"	           ^\n" +
	    		"Syntax error on token \"(\", delete this token\n" +
	    		"----------\n" +
	    		"1 problem (1 error)\n");
	    testCompileMultifile("logicalcollections", true, "", "");
	    testCompileAndRunMultifile("fractions", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| +-- FractionContainerTest [OK]\n"
	    		+ "| | +-- testAdd() [OK]\n"
	    		+ "| | +-- testFinancial() [OK]\n"
	    		+ "| | '-- testEquals() [OK]\n"
	    		+ "| '-- FractionTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         5 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         5 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         5 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         4 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         4 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         4 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("teams", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| '-- TeamsTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         4 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         4 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         4 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         1 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         1 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         1 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("bigteams", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| '-- BigTeamsTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         4 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         4 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         4 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         1 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         1 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         1 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("bigteams_nested_abs", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| '-- BigTeamsTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         4 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         4 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         4 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         1 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         1 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         1 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("html", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| '-- HtmlTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         4 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         4 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         4 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         1 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         1 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         1 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("networks", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| +-- NodeAppearancesTest [OK]\n"
	    		+ "| | '-- test() [OK]\n"
	    		+ "| '-- NodesTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         5 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         5 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         5 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         2 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         2 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         2 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("exams_rooms", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| '-- ExamsRoomsTest [OK]\n"
	    		+ "|   '-- test() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[         4 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[         4 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[         4 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[         1 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[         1 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[         1 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRunMultifile("drawit", true,
	    		".\n"
	    		+ "+-- JUnit Jupiter [OK]\n"
	    		+ "| +-- ExtentOfLeftTopWidthHeightTest [OK]\n"
	    		+ "| | +-- testGetRight() [OK]\n"
	    		+ "| | +-- testGetWidth() [OK]\n"
	    		+ "| | +-- testGetTopLeft() [OK]\n"
	    		+ "| | +-- testGetLeft() [OK]\n"
	    		+ "| | +-- testWithLeft() [OK]\n"
	    		+ "| | +-- testContains() [OK]\n"
	    		+ "| | +-- testWithTop() [OK]\n"
	    		+ "| | +-- testWithBottom() [OK]\n"
	    		+ "| | +-- testGetBottom() [OK]\n"
	    		+ "| | +-- testWithHeight() [OK]\n"
	    		+ "| | +-- testGetHeight() [OK]\n"
	    		+ "| | +-- testGetTop() [OK]\n"
	    		+ "| | +-- testWithRight() [OK]\n"
	    		+ "| | +-- testWithWidth() [OK]\n"
	    		+ "| | '-- testGetBottomRight() [OK]\n"
	    		+ "| +-- ShapeGroupTest_LeavesOnly_NoSetExtent [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | '-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| +-- ShapeGroupTest_LeavesOnly_NoSetExtent [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | '-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| +-- ShapeGroupTest_Nonleaves_1Level_setExtent [OK]\n"
	    		+ "| | +-- testSendToBack1() [OK]\n"
	    		+ "| | +-- testSendToBack2() [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroupAt() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testGetSubgroup() [OK]\n"
	    		+ "| | +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroups() [OK]\n"
	    		+ "| | +-- testBringToFront1() [OK]\n"
	    		+ "| | +-- testBringToFront2() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| | '-- testGetSubgroupCount() [OK]\n"
	    		+ "| +-- ExtentOfLeftTopRightBottomTest [OK]\n"
	    		+ "| | +-- testGetRight() [OK]\n"
	    		+ "| | +-- testGetWidth() [OK]\n"
	    		+ "| | +-- testToString() [OK]\n"
	    		+ "| | +-- testGetTopLeft() [OK]\n"
	    		+ "| | +-- testGetLeft() [OK]\n"
	    		+ "| | +-- testWithLeft() [OK]\n"
	    		+ "| | +-- testEqualsObject() [OK]\n"
	    		+ "| | +-- testContains() [OK]\n"
	    		+ "| | +-- testHashCode() [OK]\n"
	    		+ "| | +-- testWithTop() [OK]\n"
	    		+ "| | +-- testWithBottom() [OK]\n"
	    		+ "| | +-- testGetBottom() [OK]\n"
	    		+ "| | +-- testWithHeight() [OK]\n"
	    		+ "| | +-- testGetHeight() [OK]\n"
	    		+ "| | +-- testGetTop() [OK]\n"
	    		+ "| | +-- testWithRight() [OK]\n"
	    		+ "| | +-- testWithWidth() [OK]\n"
	    		+ "| | '-- testGetBottomRight() [OK]\n"
	    		+ "| +-- ShapeGroupTest_LeavesOnly_SetExtent [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | '-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| +-- RoundedPolygonTest [OK]\n"
	    		+ "| | +-- testSetVertices_improper() [OK]\n"
	    		+ "| | +-- testContains_true_on_edge() [OK]\n"
	    		+ "| | +-- testContains_false() [OK]\n"
	    		+ "| | +-- testUpdate_improper() [OK]\n"
	    		+ "| | +-- testRemove_proper() [OK]\n"
	    		+ "| | +-- testGetters() [OK]\n"
	    		+ "| | +-- testRemove_improper() [OK]\n"
	    		+ "| | +-- testContains_true_interior() [OK]\n"
	    		+ "| | +-- testPreciseRoundedPolygonContainsTestStrategy() [OK]\n"
	    		+ "| | +-- testSetVertices_proper() [OK]\n"
	    		+ "| | +-- testFastRoundedPolygonContainsTestStrategy() [OK]\n"
	    		+ "| | +-- testUpdate_proper() [OK]\n"
	    		+ "| | +-- testContains_true_vertex() [OK]\n"
	    		+ "| | +-- testSetRadius() [OK]\n"
	    		+ "| | '-- testInsert_proper() [OK]\n"
	    		+ "| +-- PointArraysTest [OK]\n"
	    		+ "| | +-- testCopy() [OK]\n"
	    		+ "| | +-- testCheckDefinesProperPolygon_coincidingVertices() [OK]\n"
	    		+ "| | +-- testCheckDefinesProperPolygon_proper() [OK]\n"
	    		+ "| | +-- testCheckDefinesProperPolygon_vertexOnEdge() [OK]\n"
	    		+ "| | +-- testInsert() [OK]\n"
	    		+ "| | +-- testCheckDefinesProperPolygon_intersectingEdges() [OK]\n"
	    		+ "| | +-- testRemove() [OK]\n"
	    		+ "| | '-- testUpdate() [OK]\n"
	    		+ "| +-- ShapeGroupTest_Nonleaves_1Level_setExtent [OK]\n"
	    		+ "| | +-- testSendToBack1() [OK]\n"
	    		+ "| | +-- testSendToBack2() [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroupAt() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testGetSubgroup() [OK]\n"
	    		+ "| | +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroups() [OK]\n"
	    		+ "| | +-- testBringToFront1() [OK]\n"
	    		+ "| | +-- testBringToFront2() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| | '-- testGetSubgroupCount() [OK]\n"
	    		+ "| +-- ShapeGroupTest_LeavesOnly_SetExtent [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | '-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| +-- IntPointTest [OK]\n"
	    		+ "| | +-- testMinus() [OK]\n"
	    		+ "| | +-- testPlus() [OK]\n"
	    		+ "| | +-- testConstructorAndGetters() [OK]\n"
	    		+ "| | +-- testLineSegmentsIntersect() [OK]\n"
	    		+ "| | +-- testIsOnLineSegment() [OK]\n"
	    		+ "| | +-- testAsDoublePoint() [OK]\n"
	    		+ "| | '-- testEquals() [OK]\n"
	    		+ "| +-- ExtentOfLeftTopRightBottomTest [OK]\n"
	    		+ "| | +-- testGetRight() [OK]\n"
	    		+ "| | +-- testGetWidth() [OK]\n"
	    		+ "| | +-- testToString() [OK]\n"
	    		+ "| | +-- testGetTopLeft() [OK]\n"
	    		+ "| | +-- testGetLeft() [OK]\n"
	    		+ "| | +-- testWithLeft() [OK]\n"
	    		+ "| | +-- testEqualsObject() [OK]\n"
	    		+ "| | +-- testContains() [OK]\n"
	    		+ "| | +-- testHashCode() [OK]\n"
	    		+ "| | +-- testWithTop() [OK]\n"
	    		+ "| | +-- testWithBottom() [OK]\n"
	    		+ "| | +-- testGetBottom() [OK]\n"
	    		+ "| | +-- testWithHeight() [OK]\n"
	    		+ "| | +-- testGetHeight() [OK]\n"
	    		+ "| | +-- testGetTop() [OK]\n"
	    		+ "| | +-- testWithRight() [OK]\n"
	    		+ "| | +-- testWithWidth() [OK]\n"
	    		+ "| | '-- testGetBottomRight() [OK]\n"
	    		+ "| +-- ShapeGroupTest_Nonleaves_1Level [OK]\n"
	    		+ "| | +-- testSendToBack1() [OK]\n"
	    		+ "| | +-- testSendToBack2() [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroupAt() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testGetSubgroup() [OK]\n"
	    		+ "| | +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroups() [OK]\n"
	    		+ "| | +-- testBringToFront1() [OK]\n"
	    		+ "| | +-- testBringToFront2() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| | '-- testGetSubgroupCount() [OK]\n"
	    		+ "| +-- ShapeGroupTest_Nonleaves_2Levels [OK]\n"
	    		+ "| | +-- testSendToBack1() [OK]\n"
	    		+ "| | +-- testSendToBack2() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_contains() [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_toGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetDrawingCommands() [OK]\n"
	    		+ "| | +-- testGetSubgroupAt() [OK]\n"
	    		+ "| | +-- testExporter() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testGetSubgroup() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_move_bottomRight() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_move() [OK]\n"
	    		+ "| | +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_getLocation() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_toShapeCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_getLocation() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_getters() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_remove() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_contains() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroups() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_toGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_toShapeCoordinates() [OK]\n"
	    		+ "| | +-- testBringToFront1() [OK]\n"
	    		+ "| | +-- testBringToFront2() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_getters() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_move_upperLeft() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| | '-- testGetSubgroupCount() [OK]\n"
	    		+ "| +-- ShapeGroupTest_Nonleaves_2Levels [OK]\n"
	    		+ "| | +-- testSendToBack1() [OK]\n"
	    		+ "| | +-- testSendToBack2() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_contains() [OK]\n"
	    		+ "| | +-- testGetShape() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_toGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testGetOriginalExtent() [OK]\n"
	    		+ "| | +-- testGetDrawingCommands() [OK]\n"
	    		+ "| | +-- testGetSubgroupAt() [OK]\n"
	    		+ "| | +-- testGetParentGroup() [OK]\n"
	    		+ "| | +-- testGetSubgroup() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_move_bottomRight() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_move() [OK]\n"
	    		+ "| | +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_getLocation() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_toShapeCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_getLocation() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_getters() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_createControlPoints_remove() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_contains() [OK]\n"
	    		+ "| | +-- testGetExtent() [OK]\n"
	    		+ "| | +-- testGetSubgroups() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_toGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_toShapeCoordinates() [OK]\n"
	    		+ "| | +-- testBringToFront1() [OK]\n"
	    		+ "| | +-- testBringToFront2() [OK]\n"
	    		+ "| | +-- testRoundedPolygonShape_getters() [OK]\n"
	    		+ "| | +-- testShapeGroupShape_createControlPoints_move_upperLeft() [OK]\n"
	    		+ "| | +-- testToGlobalCoordinates() [OK]\n"
	    		+ "| | +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "| | '-- testGetSubgroupCount() [OK]\n"
	    		+ "| +-- ExtentOfLeftTopWidthHeightTest [OK]\n"
	    		+ "| | +-- testGetRight() [OK]\n"
	    		+ "| | +-- testGetWidth() [OK]\n"
	    		+ "| | +-- testGetTopLeft() [OK]\n"
	    		+ "| | +-- testGetLeft() [OK]\n"
	    		+ "| | +-- testWithLeft() [OK]\n"
	    		+ "| | +-- testContains() [OK]\n"
	    		+ "| | +-- testWithTop() [OK]\n"
	    		+ "| | +-- testWithBottom() [OK]\n"
	    		+ "| | +-- testGetBottom() [OK]\n"
	    		+ "| | +-- testWithHeight() [OK]\n"
	    		+ "| | +-- testGetHeight() [OK]\n"
	    		+ "| | +-- testGetTop() [OK]\n"
	    		+ "| | +-- testWithRight() [OK]\n"
	    		+ "| | +-- testWithWidth() [OK]\n"
	    		+ "| | '-- testGetBottomRight() [OK]\n"
	    		+ "| +-- IntVectorTest [OK]\n"
	    		+ "| | +-- testAsDoubleVector() [OK]\n"
	    		+ "| | +-- testConstructorAndGetters() [OK]\n"
	    		+ "| | +-- testIsCollinearWith() [OK]\n"
	    		+ "| | +-- testDotProduct() [OK]\n"
	    		+ "| | '-- testCrossProduct() [OK]\n"
	    		+ "| +-- DoubleVectorTest [OK]\n"
	    		+ "| | +-- testScale() [OK]\n"
	    		+ "| | +-- testPlus() [OK]\n"
	    		+ "| | +-- testAsAngle() [OK]\n"
	    		+ "| | +-- testConstructorAndGetters() [OK]\n"
	    		+ "| | +-- testGetSize() [OK]\n"
	    		+ "| | +-- testDotProduct() [OK]\n"
	    		+ "| | '-- testCrossProduct() [OK]\n"
	    		+ "| +-- DoublePointTest [OK]\n"
	    		+ "| | +-- testMinus() [OK]\n"
	    		+ "| | +-- testRound() [OK]\n"
	    		+ "| | +-- testPlus() [OK]\n"
	    		+ "| | '-- testConstructorAndGetters() [OK]\n"
	    		+ "| '-- ShapeGroupTest_Nonleaves_1Level [OK]\n"
	    		+ "|   +-- testSendToBack1() [OK]\n"
	    		+ "|   +-- testSendToBack2() [OK]\n"
	    		+ "|   +-- testGetShape() [OK]\n"
	    		+ "|   +-- testGetOriginalExtent() [OK]\n"
	    		+ "|   +-- testGetSubgroupAt() [OK]\n"
	    		+ "|   +-- testGetParentGroup() [OK]\n"
	    		+ "|   +-- testGetSubgroup() [OK]\n"
	    		+ "|   +-- testSendToBack_bringToFront() [OK]\n"
	    		+ "|   +-- testToInnerCoordinates_IntPoint() [OK]\n"
	    		+ "|   +-- testGetExtent() [OK]\n"
	    		+ "|   +-- testGetSubgroups() [OK]\n"
	    		+ "|   +-- testBringToFront1() [OK]\n"
	    		+ "|   +-- testBringToFront2() [OK]\n"
	    		+ "|   +-- testToGlobalCoordinates() [OK]\n"
	    		+ "|   +-- testToInnerCoordinates_IntVector() [OK]\n"
	    		+ "|   '-- testGetSubgroupCount() [OK]\n"
	    		+ "+-- JUnit Vintage [OK]\n"
	    		+ "'-- JUnit Platform Suite [OK]\n"
	    		+ "\n"
	    		+ "Test run finished after XX ms\n"
	    		+ "[        23 containers found      ]\n"
	    		+ "[         0 containers skipped    ]\n"
	    		+ "[        23 containers started    ]\n"
	    		+ "[         0 containers aborted    ]\n"
	    		+ "[        23 containers successful ]\n"
	    		+ "[         0 containers failed     ]\n"
	    		+ "[       267 tests found           ]\n"
	    		+ "[         0 tests skipped         ]\n"
	    		+ "[       267 tests started         ]\n"
	    		+ "[         0 tests aborted         ]\n"
	    		+ "[       267 tests successful      ]\n"
	    		+ "[         0 tests failed          ]", "");
	    testCompileAndRun(true, "invariants_fail", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail.$classInvariants(invariants_fail.java:5)\n" + 
	    		"	at invariants_fail.<init>(invariants_fail.java:1)\n" + 
	    		"	at Main.main(invariants_fail.java:14)\n");
	    testCompileAndRun(true, "invariants_fail2", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail2.$classInvariants(invariants_fail2.java:5)\n" + 
	    		"	at invariants_fail2.getDifference(invariants_fail2.java:10)\n" + 
	    		"	at invariants_fail2.<init>(invariants_fail2.java:16)\n" + 
	    		"	at Main.main(invariants_fail2.java:24)\n");
	    testCompileAndRun(true, "invariants_fail3", true, "",
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail3.$classInvariants(invariants_fail3.java:5)\n" + 
	    		"	at invariants_fail3.foo(invariants_fail3.java:12)\n" + 
	    		"	at invariants_fail3.bar1(invariants_fail3.java:19)\n" + 
	    		"	at Main.main(invariants_fail3.java:45)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail3.$classInvariants(invariants_fail3.java:5)\n" + 
	    		"	at invariants_fail3.bar1(invariants_fail3.java:16)\n" + 
	    		"	at invariants_fail3.bar2(invariants_fail3.java:25)\n" + 
	    		"	at Main.main(invariants_fail3.java:52)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail3.$classInvariants(invariants_fail3.java:5)\n" + 
	    		"	at invariants_fail3.bar2(invariants_fail3.java:22)\n" + 
	    		"	at invariants_fail3.bar3(invariants_fail3.java:31)\n" + 
	    		"	at Main.main(invariants_fail3.java:59)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail3.$classInvariants(invariants_fail3.java:5)\n" + 
	    		"	at invariants_fail3.bar3(invariants_fail3.java:28)\n" + 
	    		"	at invariants_fail3.bar4(invariants_fail3.java:37)\n" + 
	    		"	at Main.main(invariants_fail3.java:66)\n");
	    testCompileAndRun(true, "invariants_fail4", true, "",
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail4.$packageInvariants(invariants_fail4.java:5)\n" + 
	    		"	at invariants_fail4.getDifference(invariants_fail4.java:12)\n" + 
	    		"	at invariants_fail4.foo(invariants_fail4.java:21)\n" + 
	    		"	at Main.main(invariants_fail4.java:54)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail4.$packageInvariants(invariants_fail4.java:5)\n" + 
	    		"	at invariants_fail4.foo(invariants_fail4.java:17)\n" + 
	    		"	at invariants_fail4.bar1(invariants_fail4.java:28)\n" + 
	    		"	at Main.main(invariants_fail4.java:61)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail4.$packageInvariants(invariants_fail4.java:5)\n" + 
	    		"	at invariants_fail4.bar1(invariants_fail4.java:24)\n" + 
	    		"	at invariants_fail4.bar2(invariants_fail4.java:34)\n" + 
	    		"	at Main.main(invariants_fail4.java:68)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail4.$packageInvariants(invariants_fail4.java:5)\n" + 
	    		"	at invariants_fail4.bar2(invariants_fail4.java:31)\n" + 
	    		"	at invariants_fail4.bar3(invariants_fail4.java:40)\n" + 
	    		"	at Main.main(invariants_fail4.java:75)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail4.$packageInvariants(invariants_fail4.java:5)\n" + 
	    		"	at invariants_fail4.bar3(invariants_fail4.java:37)\n" + 
	    		"	at invariants_fail4.bar4(invariants_fail4.java:46)\n" + 
	    		"	at Main.main(invariants_fail4.java:82)\n");
	    testCompileAndRun(true, "invariants_fail5", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail5.$packageInvariants(invariants_fail5.java:5)\n" + 
	    		"	at invariants_fail5.<init>(invariants_fail5.java:1)\n" + 
	    		"	at Main.main(invariants_fail5.java:14)\n");
	    testCompileAndRun(true, "invariants_fail6", true, "",
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail6.$packageInvariants(invariants_fail6.java:7)\n" + 
	    		"	at invariants_fail6.getDifference(invariants_fail6.java:13)\n" + 
	    		"	at invariants_fail6.foo(invariants_fail6.java:24)\n" + 
	    		"	at Main.main(invariants_fail6.java:57)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail6.$packageInvariants(invariants_fail6.java:7)\n" + 
	    		"	at invariants_fail6.foo(invariants_fail6.java:20)\n" + 
	    		"	at invariants_fail6.bar1(invariants_fail6.java:31)\n" + 
	    		"	at Main.main(invariants_fail6.java:64)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail6.$packageInvariants(invariants_fail6.java:7)\n" + 
	    		"	at invariants_fail6.bar1(invariants_fail6.java:27)\n" + 
	    		"	at invariants_fail6.bar2(invariants_fail6.java:37)\n" + 
	    		"	at Main.main(invariants_fail6.java:71)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail6.$packageInvariants(invariants_fail6.java:7)\n" + 
	    		"	at invariants_fail6.bar2(invariants_fail6.java:34)\n" + 
	    		"	at invariants_fail6.bar3(invariants_fail6.java:43)\n" + 
	    		"	at Main.main(invariants_fail6.java:78)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail6.$packageInvariants(invariants_fail6.java:7)\n" + 
	    		"	at invariants_fail6.bar3(invariants_fail6.java:40)\n" + 
	    		"	at invariants_fail6.bar4(invariants_fail6.java:49)\n" + 
	    		"	at Main.main(invariants_fail6.java:85)\n");
	    testCompileAndRun(true, "invariants_fail7", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError: A class representation invariant of an object must not directly or indirectly call a nonprivate method that inspects or mutates the object.\n" + 
	    		"	at invariants_fail7.$classInvariants(invariants_fail7.java:1)\n" + 
	    		"	at invariants_fail7.getDifference(invariants_fail7.java:10)\n" + 
	    		"	at invariants_fail7.$classInvariants(invariants_fail7.java:5)\n" + 
	    		"	at invariants_fail7.<init>(invariants_fail7.java:1)\n" + 
	    		"	at Main.main(invariants_fail7.java:18)\n");
	    testCompileAndRun(true, "invariants_fail8", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError: A package representation invariant of an object must not directly or indirectly call a public or protected method that inspects or mutates the object.\n" + 
	    		"	at invariants_fail8.$packageInvariants(invariants_fail8.java:1)\n" + 
	    		"	at invariants_fail8.getDifference(invariants_fail8.java:10)\n" + 
	    		"	at invariants_fail8.$packageInvariants(invariants_fail8.java:5)\n" + 
	    		"	at invariants_fail8.<init>(invariants_fail8.java:1)\n" + 
	    		"	at Main.main(invariants_fail8.java:18)\n");
	    testCompileAndRun(true, "invariants_fail9", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail9.$packageInvariants(invariants_fail9.java:2)\n" + 
	    		"	at invariants_fail9.<init>(invariants_fail9.java:4)\n" + 
	    		"	at Main.main(invariants_fail9.java:18)\n");
	    testCompileAndRun(true, "invariants_fail10", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail10.$classInvariants(invariants_fail10.java:2)\n" + 
	    		"	at invariants_fail10.<init>(invariants_fail10.java:4)\n" + 
	    		"	at Main.main(invariants_fail10.java:18)\n");
	    testCompileAndRun(true, "invariants_fail11", false, "",
	    		"Exception in thread \"main\" java.lang.AssertionError\n" + 
	    		"	at invariants_fail11.$classInvariants(invariants_fail11.java:5)\n" + 
	    		"	at invariants_fail11.foo(invariants_fail11.java:13)\n" + 
	    		"	at Main.main(invariants_fail11.java:19)\n");
	    testCompileAndRun(true, "invariants_fail12", true, "",
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail12.$classInvariants(invariants_fail12.java:5)\n" + 
	    		"	at invariants_fail12.<init>(invariants_fail12.java:13)\n" + 
	    		"	at Main.main(invariants_fail12.java:31)\n" + 
	    		"java.lang.AssertionError\n" + 
	    		"	at invariants_fail12.$classInvariants(invariants_fail12.java:5)\n" + 
	    		"	at invariants_fail12.foo(invariants_fail12.java:21)\n" + 
	    		"	at Main.main(invariants_fail12.java:38)\n");
	    
		System.out.println("s4jie2TestSuite: All tests passed.");
	}

}
