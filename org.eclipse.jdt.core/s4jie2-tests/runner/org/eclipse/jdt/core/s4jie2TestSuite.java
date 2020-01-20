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

import org.eclipse.jdt.internal.compiler.batch.Main;

/**
 * @since 3.21
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

	@SuppressWarnings("deprecation")
	public static void testCompile(String filename, boolean expectedSuccess, String outExpected, String errExpected) {
		System.out.println("     Test " + filename + " start");
		StringWriter outWriter = new StringWriter();
		StringWriter errWriter = new StringWriter();
		String path = "s4jie2-tests/src/" + filename + ".java";
		String fullPath = new File(path).getAbsolutePath();
		String args = "-source 10 -proc:none " + path + " -d " + binPath + "/" + filename;
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
		assertEquals(outWriter.toString(), outExpected.replace("SOURCE_FILE_FULL_PATH", fullPath), "standard output");
		assertEquals(errWriter.toString(), errExpected.replace("SOURCE_FILE_FULL_PATH", fullPath), "standard error");
		System.out.println("PASS Test " + filename + " compile success");
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

	public static void testCompileAndRun(String filename, boolean expectedSuccess, String outExpected, String errExpected) throws IOException {
		testCompile(filename, true, "", "");

		String classpath = binPath+"/"+filename;
		String java10Home = System.getenv("JAVA_10_HOME");
		if (java10Home == null)
			throw new AssertionError("Please make JAVA_10_HOME point to a Java 10 or later JRE or JDK");
		Process process = new ProcessBuilder(java10Home + "/bin/java", "-classpath", classpath, "Main").start();
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

	public static void main(String[] args) throws IOException {
		if (new File(binPath).exists())
			deleteFileTree(binPath);
		
		testCompile("Minimal", true, "", "");

		testCompileAndRun("GameCharacter_pre", true, "",
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
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" + 
				"	*    | amount\n" + 
				"	       ^^^^^^\n" + 
				"Type mismatch: cannot convert from int to boolean\n" + 
				"----------\n" + 
				"1 problem (1 error)\n");
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
				"3 problems (3 errors)\n");
	    testCompile("GameCharacter_pre_post_type_error", false, "",
	    		"----------\n" + 
	    		"1. ERROR in SOURCE_FILE_FULL_PATH (at line 9)\n" + 
	    		"	*     | getHealth()\n" + 
	    		"	        ^^^^^^^^^^^\n" + 
	    		"Type mismatch: cannot convert from int to boolean\n" + 
	    		"----------\n" + 
	    		"1 problem (1 error)\n");
		testCompileAndRun("GameCharacter_pre_post", true, "",
				"java.lang.AssertionError: Postcondition does not hold\n" + 
				"	at GameCharacter.takeDamage$post(GameCharacter_pre_post.java:26)\n" + 
				"	at GameCharacter.takeDamage(GameCharacter_pre_post.java:34)\n" + 
				"	at Main.main(GameCharacter_pre_post.java:96)\n" + 
				"java.lang.AssertionError: Postcondition does not hold\n" + 
				"	at GameCharacter.setHealth$post(GameCharacter_pre_post.java:11)\n" + 
				"	at GameCharacter.setHealth(GameCharacter_pre_post.java:15)\n" + 
				"	at Main.main(GameCharacter_pre_post.java:103)\n" + 
				"java.lang.AssertionError: Postcondition does not hold\n" + 
				"	at GameCharacter.simpleReturnTest$post(GameCharacter_pre_post.java:52)\n" + 
				"	at GameCharacter.simpleReturnTest(GameCharacter_pre_post.java:56)\n" + 
				"	at Main.main(GameCharacter_pre_post.java:110)\n" + 
				"java.lang.AssertionError: Postcondition does not hold\n" + 
				"	at GameCharacter.returnInsideIfTest$post(GameCharacter_pre_post.java:60)\n" + 
				"	at GameCharacter.returnInsideIfTest(GameCharacter_pre_post.java:65)\n" + 
				"	at Main.main(GameCharacter_pre_post.java:117)\n" + 
				"java.lang.AssertionError: Postcondition does not hold\n" + 
				"	at GameCharacter.returnInsideIfTest$post(GameCharacter_pre_post.java:60)\n" + 
				"	at GameCharacter.returnInsideIfTest(GameCharacter_pre_post.java:68)\n" + 
				"	at Main.main(GameCharacter_pre_post.java:124)\n");
		
		System.out.println("s4jie2TestSuite: All tests passed.");
	}

}
