/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.performance;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;

import junit.framework.Test;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeNameRequestor;
import org.eclipse.jdt.core.tests.util.Util;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.test.performance.Performance;

/**
 * Class to test compiler performance.
 * This includes tests on build, batch compiler, Scanner and Parser.
 */
public class FullSourceWorkspaceGenericsTests extends FullSourceWorkspaceTests implements IJavaSearchConstants {

	// Tests counters
	static int TESTS_COUNT = 0;
	private final static int WARMUP_COUNT = 1; // 30;
	private final static int ITERATIONS_COUNT = 30;
	static int TESTS_LENGTH;

	// Log file streams
	private static PrintStream[] LOG_STREAMS = new PrintStream[LOG_TYPES.length];

	// Search variables
	IJavaSearchScope scope;
	protected JavaSearchResultCollector resultCollector;
	
	// Type path
	static IPath BIG_PROJECT_TYPE_PATH;
	static ICompilationUnit WORKING_COPY;

/**
 * @param name
 */
public FullSourceWorkspaceGenericsTests(String name) {
	super(name);
}

static {
//	TESTS_NAMES = new String[] {
//		"testPerfNameLookupFindKnownSecondaryType",
//		"testPerfNameLookupFindUnknownType",
//		"testPerfReconcile", 
//		"testPerfSearchAllTypeNamesAndReconcile",
//	};
	
//	TESTS_PREFIX = "testPerfReconcile";
}
public static Test suite() {
	Test suite = buildSuite(testClass());
	TESTS_LENGTH = TESTS_COUNT = suite.countTestCases();
	createPrintStream(testClass(), LOG_STREAMS, TESTS_COUNT, null);
	return suite;
}

private static Class testClass() {
	return FullSourceWorkspaceGenericsTests.class;
}

protected void setUp() throws Exception {
	super.setUp();
	this.resultCollector = new JavaSearchResultCollector();
	this.scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { JDT_CORE_PROJECT });
	if (GENERICS_PROJECT == null) {
		setUpProject();
	}
	assertNotNull("We should have found "+GENERICS_PROJECT_NAME+" project in workspace!!!", GENERICS_PROJECT);
}
private void setUpProject() throws CoreException, IOException {
	IWorkspace workspace = ResourcesPlugin.getWorkspace();
	IWorkspaceRoot workspaceRoot = workspace.getRoot();
	final String targetWorkspacePath = workspaceRoot.getLocation().toFile().getCanonicalPath();

	// Print for log in case of project creation troubles...
	System.out.println("Create project "+GENERICS_PROJECT_NAME+" in "+workspaceRoot.getLocation()+":");
	long start = System.currentTimeMillis();

	// Print for log in case of project creation troubles...
	String genericsZipPath = getPluginDirectoryPath() + File.separator + "generics.zip";
	start = System.currentTimeMillis();
	System.out.println("Unzipping "+genericsZipPath);
	System.out.print("	in "+targetWorkspacePath+"...");

	// Unzip file
	Util.unzip(genericsZipPath, targetWorkspacePath);
	System.out.println(" "+(System.currentTimeMillis()-start)+"ms.");

	// Add project to workspace
	ENV.addProject(BIG_PROJECT_NAME);
	BIG_PROJECT = (JavaProject) createJavaProject(BIG_PROJECT_NAME, new String[]{ "src" }, "bin", "1.5");
	BIG_PROJECT.setRawClasspath(BIG_PROJECT.getRawClasspath(), null);

	// Print for log in case of project creation troubles...
	System.out.println("("+(System.currentTimeMillis()-start)+"ms)");
	start = System.currentTimeMillis();
}
/* (non-Javadoc)
 * @see junit.framework.TestCase#tearDown()
 */
protected void tearDown() throws Exception {

	// End of execution => one test less
	TESTS_COUNT--;

	// Log perf result
	if (LOG_DIR != null) {
		logPerfResult(LOG_STREAMS, TESTS_COUNT);
	}

	// Print statistics
	if (TESTS_COUNT == 0) {
		System.out.println("-------------------------------------");
		System.out.println("Generics performance test statistics:");
//		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		System.out.println("-------------------------------------\n");
	}
	super.tearDown();
}
/**
 * Simple search result collector: only count matches.
 */
class JavaSearchResultCollector extends SearchRequestor {
	int count = 0;
	public void acceptSearchMatch(SearchMatch match) throws CoreException {
		this.count++;
	}
}

protected void search(String patternString, int searchFor, int limitTo) throws CoreException {
	int matchMode = patternString.indexOf('*') != -1 || patternString.indexOf('?') != -1
		? SearchPattern.R_PATTERN_MATCH
		: SearchPattern.R_EXACT_MATCH;
	SearchPattern pattern = SearchPattern.createPattern(
		patternString, 
		searchFor,
		limitTo, 
		matchMode | SearchPattern.R_CASE_SENSITIVE);
	new SearchEngine().search(
		pattern,
		new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
		this.scope,
		this.resultCollector,
		null);
}

protected void searchAllTypeNames() throws CoreException {
	class TypeNameCounter extends TypeNameRequestor {
		int count = 0;
		public void acceptType(int modifiers, char[] packageName, char[] simpleTypeName, char[][] enclosingTypeNames, String path) {
			count++;
		}
	}
	TypeNameCounter requestor = new TypeNameCounter();
	new SearchEngine().searchAllTypeNames(
		null,
		null,
		SearchPattern.R_PREFIX_MATCH, // not case sensitive
		IJavaSearchConstants.TYPE,
		this.scope,
		requestor,
		WAIT_UNTIL_READY_TO_SEARCH,
		null);
	assertTrue("We should have found at least one type!", requestor.count>0);
}

/**
 * @see org.eclipse.jdt.core.tests.model.AbstractJavaModelTests#assertElementEquals(String, String, IJavaElement)
 */
protected void assertElementEquals(String message, String expected, IJavaElement element) {
	String actual = element == null ? "<null>" : ((JavaElement) element).toStringWithAncestors(false/*don't show key*/);
	if (!expected.equals(actual)) {
		System.out.println(getName()+" actual result is:");
		System.out.println(actual + ',');
	}
	assertEquals(message, expected, actual);
}
/**
 * @see org.eclipse.jdt.core.tests.model.AbstractJavaModelTests#assertElementsEqual(String, String, IJavaElement[])
 */
protected void assertElementsEqual(String message, String expected, IJavaElement[] elements) {
	assertElementsEqual(message, expected, elements, false/*don't show key*/);
}
/**
 * @see org.eclipse.jdt.core.tests.model.AbstractJavaModelTests#assertElementsEqual(String, String, IJavaElement[], boolean)
 */
protected void assertElementsEqual(String message, String expected, IJavaElement[] elements, boolean showResolvedInfo) {
	StringBuffer buffer = new StringBuffer();
	if (elements != null) {
		for (int i = 0, length = elements.length; i < length; i++){
			JavaElement element = (JavaElement)elements[i];
			if (element == null) {
				buffer.append("<null>");
			} else {
				buffer.append(element.toStringWithAncestors(showResolvedInfo));
			}
			if (i != length-1) buffer.append("\n");
		}
	} else {
		buffer.append("<null>");
	}
	String actual = buffer.toString();
	if (!expected.equals(actual)) {
		System.out.println(getName()+" actual result is:");
		System.out.println(actual + ',');
	}
	assertEquals(message, expected, actual);
}

	/**
	 * Full build with JavaCore default options.
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testFullBuild() throws CoreException, IOException {
		tagAsGlobalSummary("Compile>Build>Clean>Full>Default warnings", true); // put in fingerprint
		setComment(Performance.EXPLAINS_DEGRADATION_COMMENT, "J2SE 5.0 support + additional warnings (e.g. validate unused local and private members)");
		Hashtable<String, String> options = warningOptions(0/*default warnings*/);
		options.put(CompilerOptions.OPTION_Compliance, CompilerOptions.VERSION_1_5);
		options.put(CompilerOptions.OPTION_Source, CompilerOptions.VERSION_1_5);	
		options.put(CompilerOptions.OPTION_TargetPlatform, CompilerOptions.VERSION_1_5);	
		startBuild(GENERICS_PROJECT.getProject().getFullPath(), options, false);
	}
}
