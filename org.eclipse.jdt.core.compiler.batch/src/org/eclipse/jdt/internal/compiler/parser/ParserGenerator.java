package org.eclipse.jdt.internal.compiler.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class ParserGenerator {
	final static String FILEPREFIX = "parser"; //$NON-NLS-1$
	final static String READABLE_NAMES_FILE = "readableNames"; //$NON-NLS-1$
	private static final String EOF_TOKEN = "$eof" ; //$NON-NLS-1$
	private static final String ERROR_TOKEN = "$error" ; //$NON-NLS-1$
	private static final String INVALID_CHARACTER = "Invalid Character" ; //$NON-NLS-1$
	private static final String UNEXPECTED_EOF = "Unexpected End Of File" ; //$NON-NLS-1$
	private static int getSymbol(String terminalName, String[] newName, int[] newReverse) {
		for (int j = 0; j < newName.length; j++) {
			if(terminalName.equals(newName[j])) {
				return newReverse[j];
			}
		}
		return -1;
	}
	private final static void buildFile(String filename, List listToDump) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filename));
	    	for (Iterator iterator = listToDump.iterator(); iterator.hasNext(); ) {
	    		writer.write(String.valueOf(iterator.next()));
	    	}
	    	writer.flush();
		} catch(IOException e) {
			// ignore
		} finally {
			if (writer != null) {
	        	try {
					writer.close();
				} catch (IOException e1) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private static void buildFileForCompliance(
			String file,
			int length,
			String[] tokens) {

			byte[] result = new byte[length * 8];

			for (int i = 0; i < tokens.length; i = i + 3) {
				if("2".equals(tokens[i])) { //$NON-NLS-1$
					int index = Integer.parseInt(tokens[i + 1]);
					String token = tokens[i + 2].trim();
					long compliance = 0;
					if("1.4".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_4;
					} else if("1.5".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_5;
					} else if("1.6".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_6;
					} else if("1.7".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_7;
					} else if("1.8".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK1_8;
					}  else if("9".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK9;
					}  else if("10".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK10;
					}  else if("11".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK11;
					}  else if("12".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK12;
					}  else if("13".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK13;
					}  else if("14".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK14;
					}  else if("15".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK15;
					}  else if("16".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK16;
					}  else if("17".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK17;
					}  else if("18".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK18;
					}  else if("19".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK19;
					}  else if("20".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK20;
					}  else if("21".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK21;
					} else if("recovery".equals(token)) { //$NON-NLS-1$
						compliance = ClassFileConstants.JDK_DEFERRED;
					}

					int j = index * 8;
					result[j] = 	(byte)(compliance >>> 56);
					result[j + 1] = (byte)(compliance >>> 48);
					result[j + 2] = (byte)(compliance >>> 40);
					result[j + 3] = (byte)(compliance >>> 32);
					result[j + 4] = (byte)(compliance >>> 24);
					result[j + 5] = (byte)(compliance >>> 16);
					result[j + 6] = (byte)(compliance >>> 8);
					result[j + 7] = (byte)(compliance);
				}
			}

			buildFileForTable(file, result);
		}
	private final static String[] buildFileForName(String filename, String contents) {
		String[] result = new String[contents.length()];
		result[0] = null;
		int resultCount = 1;

		StringBuilder buffer = new StringBuilder();

		int start = contents.indexOf("name[]"); //$NON-NLS-1$
		start = contents.indexOf('\"', start);
		int end = contents.indexOf("};", start); //$NON-NLS-1$

		contents = contents.substring(start, end);

		boolean addLineSeparator = false;
		int tokenStart = -1;
		StringBuilder currentToken = new StringBuilder();
		for (int i = 0; i < contents.length(); i++) {
			char c = contents.charAt(i);
			if(c == '\"') {
				if(tokenStart == -1) {
					tokenStart = i + 1;
				} else {
					if(addLineSeparator) {
						buffer.append('\n');
						result[resultCount++] = currentToken.toString();
						currentToken = new StringBuilder();
					}
					String token = contents.substring(tokenStart, i);
					if(token.equals(ERROR_TOKEN)){
						token = INVALID_CHARACTER;
					} else if(token.equals(EOF_TOKEN)) {
						token = UNEXPECTED_EOF;
					}
					buffer.append(token);
					currentToken.append(token);
					addLineSeparator = true;
					tokenStart = -1;
				}
			}
			if(tokenStart == -1 && c == '+'){
				addLineSeparator = false;
			}
		}
		if(currentToken.length() > 0) {
			result[resultCount++] = currentToken.toString();
		}

		buildFileForTable(filename, buffer.toString().toCharArray());

		System.arraycopy(result, 0, result = new String[resultCount], 0, resultCount);
		return result;
	}
	private static void buildFileForReadableName(
		String file,
		char[] newLhs,
		char[] newNonTerminalIndex,
		String[] newName,
		String[] tokens) {

		ArrayList entries = new ArrayList();

		boolean[] alreadyAdded = new boolean[newName.length];

		for (int i = 0; i < tokens.length; i = i + 3) {
			if("1".equals(tokens[i])) { //$NON-NLS-1$
				int index = newNonTerminalIndex[newLhs[Integer.parseInt(tokens[i + 1])]];
				StringBuffer buffer = new StringBuffer();
				if(!alreadyAdded[index]) {
					alreadyAdded[index] = true;
					buffer.append(newName[index]);
					buffer.append('=');
					buffer.append(tokens[i+2].trim());
					buffer.append('\n');
					entries.add(String.valueOf(buffer));
				}
			}
		}
		int i = 1;
		while(!INVALID_CHARACTER.equals(newName[i])) i++;
		i++;
		for (; i < alreadyAdded.length; i++) {
			if(!alreadyAdded[i]) {
				System.out.println(newName[i] + " has no readable name"); //$NON-NLS-1$
			}
		}
		Collections.sort(entries);
		buildFile(file, entries);
	}
	private final static void buildFileForTable(String filename, byte[] bytes) {
		java.io.FileOutputStream stream = null;
		try {
			stream = new java.io.FileOutputStream(filename);
			stream.write(bytes);
		} catch(IOException e) {
			// ignore
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private final static void buildFileForTable(String filename, char[] chars) {
		byte[] bytes = new byte[chars.length * 2];
		for (int i = 0; i < chars.length; i++) {
			bytes[2 * i] = (byte) (chars[i] >>> 8);
			bytes[2 * i + 1] = (byte) (chars[i] & 0xFF);
		}

		java.io.FileOutputStream stream = null;
		try {
			stream = new java.io.FileOutputStream(filename);
			stream.write(bytes);
		} catch(IOException e) {
			// ignore
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		System.out.println(filename + " creation complete"); //$NON-NLS-1$
	}
	private final static byte[] buildFileOfByteFor(String filename, String tag, String[] tokens) {

		//transform the String tokens into chars before dumping then into file

		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }

		byte[] bytes = new byte[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			bytes[ic++] = (byte) c;
		}

		//resize
		System.arraycopy(bytes, 0, bytes = new byte[ic], 0, ic);

		buildFileForTable(filename, bytes);
		return bytes;
	}
	private final static char[] buildFileOfIntFor(String filename, String tag, String[] tokens) {

		//transform the String tokens into chars before dumping then into file

		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }

		char[] chars = new char[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			chars[ic++] = (char) c;
		}

		//resize
		System.arraycopy(chars, 0, chars = new char[ic], 0, ic);

		buildFileForTable(filename, chars);
		return chars;
	}
	private final static void buildFileOfShortFor(String filename, String tag, String[] tokens) {

		//transform the String tokens into chars before dumping then into file

		int i = 0;
		//read upto the tag
		while (!tokens[i++].equals(tag)){/*empty*/}
		//read upto the }

		char[] chars = new char[tokens.length]; //can't be bigger
		int ic = 0;
		String token;
		while (!(token = tokens[i++]).equals("}")) { //$NON-NLS-1$
			int c = Integer.parseInt(token);
			chars[ic++] = (char) (c + 32768);
		}

		//resize
		System.arraycopy(chars, 0, chars = new char[ic], 0, ic);

		buildFileForTable(filename, chars);
	}
	private static void buildFilesForRecoveryTemplates(
		String indexFilename,
		String templatesFilename,
		char[] newTerminalIndex,
		char[] newNonTerminalIndex,
		String[] newName,
		char[] newLhs,
		String[] tokens) {

		int[] newReverse = computeReverseTable(newTerminalIndex, newNonTerminalIndex, newName);

		char[] newRecoveyTemplatesIndex = new char[newNonTerminalIndex.length];
		char[] newRecoveyTemplates = new char[newNonTerminalIndex.length];
		int newRecoveyTemplatesPtr = 0;

		for (int i = 0; i < tokens.length; i = i + 3) {
			if("3".equals(tokens[i])) { //$NON-NLS-1$
				int length = newRecoveyTemplates.length;
				if(length == newRecoveyTemplatesPtr + 1) {
					System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
				}
				newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;

				int index = newLhs[Integer.parseInt(tokens[i + 1])];

				newRecoveyTemplatesIndex[index] = (char)newRecoveyTemplatesPtr;

				String token = tokens[i + 2].trim();
				java.util.StringTokenizer st = new java.util.StringTokenizer(token, " ");  //$NON-NLS-1$
				String[] terminalNames = new String[st.countTokens()];
				int t = 0;
				while (st.hasMoreTokens()) {
					terminalNames[t++] = st.nextToken();
				}

				for (int j = 0; j < terminalNames.length; j++) {
					int symbol = getSymbol(terminalNames[j], newName, newReverse);
					if(symbol > -1) {
						length = newRecoveyTemplates.length;
						if(length == newRecoveyTemplatesPtr + 1) {
							System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[length * 2], 0, length);
						}
						newRecoveyTemplates[newRecoveyTemplatesPtr++] = (char)symbol;
					}
				}
			}
		}
		newRecoveyTemplates[newRecoveyTemplatesPtr++] = 0;
		System.arraycopy(newRecoveyTemplates, 0, newRecoveyTemplates = new char[newRecoveyTemplatesPtr], 0, newRecoveyTemplatesPtr);

		buildFileForTable(indexFilename, newRecoveyTemplatesIndex);
		buildFileForTable(templatesFilename, newRecoveyTemplates);
	}
	private static void buildFilesForStatementsRecoveryFilter(
			String filename,
			char[] newNonTerminalIndex,
			char[] newLhs,
			String[] tokens) {

			char[] newStatementsRecoveryFilter = new char[newNonTerminalIndex.length];

			for (int i = 0; i < tokens.length; i = i + 3) {
				if("4".equals(tokens[i])) { //$NON-NLS-1$
					int index = newLhs[Integer.parseInt(tokens[i + 1])];

					newStatementsRecoveryFilter[index] = 1;
				}
			}
			buildFileForTable(filename, newStatementsRecoveryFilter);
		}
	public final static void buildFilesFromLPG(String dataFilename, String dataFilename2) {

		//RUN THIS METHOD TO GENERATE PARSER*.RSC FILES

		//build from the lpg javadcl.java files that represents the parser tables
		//lhs check_table asb asr symbol_index

		//[org.eclipse.jdt.internal.compiler.parser.Parser.buildFilesFromLPG("d:/leapfrog/grammar/javadcl.java")]
		char[] contents = CharOperation.NO_CHAR;
		try {
			contents = Util.getFileCharContent(new File(dataFilename), null);
		} catch (IOException ex) {
			System.out.println(Messages.parser_incorrectPath);
			return;
		}
		java.util.StringTokenizer st =
			new java.util.StringTokenizer(new String(contents), " \t\n\r[]={,;");  //$NON-NLS-1$
		String[] tokens = new String[st.countTokens()];
		int j = 0;
		while (st.hasMoreTokens()) {
			tokens[j++] = st.nextToken();
		}
		final String prefix = FILEPREFIX;
		int i = 0;

		char[] newLhs = buildFileOfIntFor(prefix + (++i) + ".rsc", "lhs", tokens); //$NON-NLS-1$ //$NON-NLS-2$
		buildFileOfShortFor(prefix + (++i) + ".rsc", "check_table", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "asb", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "asr", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "nasb", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "nasr", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		char[] newTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "terminal_index", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		char[] newNonTerminalIndex = buildFileOfIntFor(prefix + (++i) + ".rsc", "non_terminal_index", tokens); //$NON-NLS-1$ //$NON-NLS-2$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "term_action", tokens); //$NON-NLS-2$ //$NON-NLS-1$

		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_prefix", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_suffix", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_lhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state_set", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_rhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_state", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "in_symb", tokens); //$NON-NLS-2$ //$NON-NLS-1$

		byte[] newRhs = buildFileOfByteFor(prefix + (++i) + ".rsc", "rhs", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "term_check", tokens); //$NON-NLS-2$ //$NON-NLS-1$
		buildFileOfIntFor(prefix + (++i) + ".rsc", "scope_la", tokens); //$NON-NLS-2$ //$NON-NLS-1$

		String[] newName = buildFileForName(prefix + (++i) + ".rsc", new String(contents)); //$NON-NLS-1$

		contents = CharOperation.NO_CHAR;
		try {
			contents = Util.getFileCharContent(new File(dataFilename2), null);
		} catch (IOException ex) {
			System.out.println(Messages.parser_incorrectPath);
			return;
		}
		st = new java.util.StringTokenizer(new String(contents), "\t\n\r#");  //$NON-NLS-1$
		tokens = new String[st.countTokens()];
		j = 0;
		while (st.hasMoreTokens()) {
			tokens[j++] = st.nextToken();
		}

		buildFileForCompliance(prefix + (++i) + ".rsc", newRhs.length, tokens);//$NON-NLS-1$
		buildFileForReadableName(READABLE_NAMES_FILE+".props", newLhs, newNonTerminalIndex, newName, tokens);//$NON-NLS-1$

		buildFilesForRecoveryTemplates(
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				newTerminalIndex,
				newNonTerminalIndex,
				newName,
				newLhs,
				tokens);

		buildFilesForStatementsRecoveryFilter(
				prefix + (++i) + ".rsc", //$NON-NLS-1$
				newNonTerminalIndex,
				newLhs,
				tokens);


		System.out.println(Messages.parser_moveFiles);
	}
	protected static int[] computeReverseTable(char[] newTerminalIndex, char[] newNonTerminalIndex, String[] newName) {
		int[] newReverseTable = new int[newName.length];
		for (int j = 0; j < newName.length; j++) {
			found : {
				for (int k = 0; k < newTerminalIndex.length; k++) {
					if(newTerminalIndex[k] == j) {
						newReverseTable[j] = k;
						break found;
					}
				}
				for (int k = 0; k < newNonTerminalIndex.length; k++) {
					if(newNonTerminalIndex[k] == j) {
						newReverseTable[j] = -k;
						break found;
					}
				}
			}
		}
		return newReverseTable;
	}

}
