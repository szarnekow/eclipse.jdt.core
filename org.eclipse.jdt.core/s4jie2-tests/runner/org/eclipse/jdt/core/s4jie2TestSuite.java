package org.eclipse.jdt.core;

import java.io.File;
import java.io.IOException;
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
	public static void test(String filename, boolean expectedSuccess, String outExpected, String errExpected) {
		System.out.println("     Test " + filename + " start");
		StringWriter outWriter = new StringWriter();
		StringWriter errWriter = new StringWriter();
		String path = "s4jie2-tests/src/" + filename + ".java";
		String fullPath = new File(path).getAbsolutePath();
		String args = "-source 1.8 -proc:none " + path + " -d " + binPath + "/" + filename;
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
		System.out.println("PASS Test " + filename + " success");
	}

	public static void main(String[] args) throws IOException {
		deleteFileTree(binPath);
		
		test("Minimal", true, "", "");
		
		test("GameCharacter_pre", true, "", "");
		test("GameCharacter_pre_fail", false, "",
				"----------\n" + 
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" + 
				"	*    | 0 <=\n" + 
				"	         ^^\n" + 
				"Syntax error on token \"<=\", Expression expected after this token\n" + 
				"----------\n" + 
				"1 problem (1 error)\n");
		test("GameCharacter_pre_type_error", false, "",
				"----------\n" + 
				"1. ERROR in SOURCE_FILE_FULL_PATH (at line 10)\n" + 
				"	*    | amount\n" + 
				"	       ^^^^^^\n" + 
				"Type mismatch: cannot convert from int to boolean\n" + 
				"----------\n" + 
				"1 problem (1 error)\n");
		test("GameCharacter_pre_post", true, "", "");
		test("GameCharacter_pre_post_syntax_error", false, "",
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
		
		System.out.println("s4jie2TestSuite: All tests passed.");
	}

}
