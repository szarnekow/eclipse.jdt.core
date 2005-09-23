/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.core.tests.model;

import java.util.*;

import org.eclipse.jdt.core.ICorrectionRequestor;

public class CodeCorrectionTestsRequestor implements ICorrectionRequestor {
	private class Suggestion {
		public String text;
		public int start;
		public int end;
		public Suggestion(char[] text, int start, int end){
			this.text = new String(text);
			this.start = start;
			this.end = end;
		}
	}
	
	private class SuggestionComparator<T extends Suggestion> implements Comparator<T> {
		public int compare(T o1,T o2) {
			int result = o1.text.compareTo(o2.text);
			if(result == 0) {
				result = o1.start - o2.start;
				if(result == 0) {
					result = o1.end - o2.end;	
				}
			}
			return result;
		}
	}
	
	
	private Vector fSuggestions = new Vector(5);
	
	public void acceptClass(char[] packageName,char[] className,char[] correctionName,int modifiers,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(correctionName, correctionStart, correctionEnd));
	}
	
	public void acceptField(char[] declaringTypePackageName,char[] declaringTypeName,char[] name,char[] typePackageName,char[] typeName,char[] correctionName,int modifiers,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(correctionName, correctionStart, correctionEnd));
	}
	
	public void acceptInterface(char[] packageName,char[] interfaceName,char[] correctionName,int modifiers,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(correctionName, correctionStart, correctionEnd));
	}
	
	public void acceptLocalVariable(char[] name,char[] typePackageName,char[] typeName,int modifiers,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(name, correctionStart, correctionEnd));
	}
	
	public void acceptMethod(char[] declaringTypePackageName,char[] declaringTypeName,char[] selector,char[][] parameterPackageNames,char[][] parameterTypeNames,char[][] parameterNames,char[] returnTypePackageName,char[] returnTypeName,char[] correctionName,int modifiers,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(correctionName, correctionStart, correctionEnd));
	}
	
	public void acceptPackage(char[] packageName,char[] correctionName,int correctionStart,int correctionEnd){
		fSuggestions.addElement(new Suggestion(correctionName, correctionStart, correctionEnd));
	}
	
	public String getSuggestions(){
		Suggestion[] suggestions = getSortedSuggestions();
		
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < suggestions.length; i++) {
			if(i != 0)
				result.append('\n');
				
			result.append(suggestions[i].text);
		}
		return result.toString();
	}
	
	public String getStarts(){
		Suggestion[] suggestions = getSortedSuggestions();
		
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < suggestions.length; i++) {
			if(i != 0)
				result.append('\n');
				
			result.append(suggestions[i].start);
		}
		return result.toString();
	}
	
	public String getEnds(){
		Suggestion[] suggestions = getSortedSuggestions();
		
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < suggestions.length; i++) {
			if(i != 0)
				result.append('\n');
				
			result.append(suggestions[i].end);
		}
		return result.toString();
	}
	
	private Suggestion[] getSortedSuggestions(){
		Object[] unsorted = fSuggestions.toArray();
		Suggestion[] sorted = new Suggestion[unsorted.length];
		System.arraycopy(unsorted, 0, sorted, 0, unsorted.length);
		Arrays.<Suggestion>sort(sorted, new SuggestionComparator<Suggestion>());
		return sorted;
	}
}
