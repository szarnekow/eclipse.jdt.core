/*******************************************************************************
 * Copyright (c) 2000, 2022 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.parser;

/**
 * IMPORTANT NOTE: These constants are dedicated to the internal Scanner implementation.
 * It is mirrored in org.eclipse.jdt.core.compiler public package where it is API.
 * The mirror implementation is using the backward compatible ITerminalSymbols constant
 * definitions (stable with 2.0), whereas the internal implementation uses TerminalTokens
 * which constant values reflect the latest parser generation state.
 */
/**
 * Maps each terminal symbol in the java-grammar into a unique integer.
 * This integer is used to represent the terminal when computing a parsing action.
 *
 * Disclaimer : These constant values are generated automatically using a Java
 * grammar, therefore their actual values are subject to change if new keywords
 * were added to the language (for instance, 'assert' is a keyword in 1.4).
 */
public interface TerminalTokens {

	// special tokens not part of grammar - not autogenerated
	int TokenNameNotAToken = 0,
							TokenNameWHITESPACE = 1000,
							TokenNameCOMMENT_LINE = 1001,
							TokenNameCOMMENT_BLOCK = 1002,
							TokenNameCOMMENT_JAVADOC = 1003,
							TokenNameSingleQuoteStringLiteral = 1004;

	static boolean isRestrictedKeyword(int tokenType) {
		return switch (tokenType) {
			case TokenNameRestrictedIdentifierYield, TokenNameRestrictedIdentifierrecord,TokenNameRestrictedIdentifierWhen,
					TokenNameRestrictedIdentifiersealed, TokenNameRestrictedIdentifierpermits -> true;
			default -> false;
		};
	}

	static int getRestrictedKeyword(char [] text) {
		if (text != null) {
			int len = text.length;
			if (len == 4 && text[0] == 'w' ||
				len == 5 && text[0] == 'y' ||
				len == 6 && (text[0] == 'r' || text[0] == 's') ||
				len == 7 && text[0] == 'p') {
				return getRestrictedKeyword(new String(text));
			}
		}
		return TokenNameNotAToken;
	}

	static int getRestrictedKeyword(String text) {
		return switch (text) {
			case "yield"   -> TokenNameRestrictedIdentifierYield;   //$NON-NLS-1$
			case "record"  -> TokenNameRestrictedIdentifierrecord;  //$NON-NLS-1$
			case "when"    -> TokenNameRestrictedIdentifierWhen;    //$NON-NLS-1$
			case "sealed"  -> TokenNameRestrictedIdentifiersealed;  //$NON-NLS-1$
			case "permits" -> TokenNameRestrictedIdentifierpermits; //$NON-NLS-1$
			default        -> TokenNameNotAToken;
		};
	}

	// BEGIN_AUTOGENERATED_REGION
	int TokenNameIdentifier = 19,
							TokenNameabstract = 42,
							TokenNameassert = 85,
							TokenNameboolean = 109,
							TokenNamebreak = 86,
							TokenNamebyte = 110,
							TokenNamecase = 95,
							TokenNamecatch = 111,
							TokenNamechar = 112,
							TokenNameclass = 72,
							TokenNamecontinue = 87,
							TokenNameconst = 140,
							TokenNamedefault = 80,
							TokenNamedo = 88,
							TokenNamedouble = 113,
							TokenNameelse = 125,
							TokenNameenum = 78,
							TokenNameextends = 96,
							TokenNamefalse = 53,
							TokenNamefinal = 43,
							TokenNamefinally = 120,
							TokenNamefloat = 114,
							TokenNamefor = 89,
							TokenNamegoto = 141,
							TokenNameif = 90,
							TokenNameimplements = 137,
							TokenNameimport = 115,
							TokenNameinstanceof = 17,
							TokenNameint = 116,
							TokenNameinterface = 77,
							TokenNamelong = 117,
							TokenNamenative = 44,
							TokenNamenew = 38,
							TokenNamenon_sealed = 45,
							TokenNamenull = 54,
							TokenNamepackage = 94,
							TokenNameprivate = 46,
							TokenNameprotected = 47,
							TokenNamepublic = 48,
							TokenNamereturn = 91,
							TokenNameshort = 118,
							TokenNamestatic = 37,
							TokenNamestrictfp = 49,
							TokenNamesuper = 34,
							TokenNameswitch = 65,
							TokenNamesynchronized = 39,
							TokenNamethis = 35,
							TokenNamethrow = 82,
							TokenNamethrows = 121,
							TokenNametransient = 50,
							TokenNametrue = 55,
							TokenNametry = 92,
							TokenNamevoid = 119,
							TokenNamevolatile = 51,
							TokenNamewhile = 83,
							TokenNamemodule = 122,
							TokenNameopen = 123,
							TokenNamerequires = 126,
							TokenNametransitive = 132,
							TokenNameexports = 127,
							TokenNameopens = 128,
							TokenNameto = 138,
							TokenNameuses = 129,
							TokenNameprovides = 130,
							TokenNamewith = 139,
							TokenNameIntegerLiteral = 56,
							TokenNameLongLiteral = 57,
							TokenNameFloatingPointLiteral = 58,
							TokenNameDoubleLiteral = 59,
							TokenNameCharacterLiteral = 60,
							TokenNameStringLiteral = 61,
							TokenNameTextBlock = 62,
							TokenNamePLUS_PLUS = 2,
							TokenNameMINUS_MINUS = 3,
							TokenNameEQUAL_EQUAL = 20,
							TokenNameLESS_EQUAL = 12,
							TokenNameGREATER_EQUAL = 13,
							TokenNameNOT_EQUAL = 21,
							TokenNameLEFT_SHIFT = 18,
							TokenNameRIGHT_SHIFT = 14,
							TokenNameUNSIGNED_RIGHT_SHIFT = 16,
							TokenNamePLUS_EQUAL = 97,
							TokenNameMINUS_EQUAL = 98,
							TokenNameMULTIPLY_EQUAL = 99,
							TokenNameDIVIDE_EQUAL = 100,
							TokenNameAND_EQUAL = 101,
							TokenNameOR_EQUAL = 102,
							TokenNameXOR_EQUAL = 103,
							TokenNameREMAINDER_EQUAL = 104,
							TokenNameLEFT_SHIFT_EQUAL = 105,
							TokenNameRIGHT_SHIFT_EQUAL = 106,
							TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL = 107,
							TokenNameOR_OR = 31,
							TokenNameAND_AND = 30,
							TokenNamePLUS = 4,
							TokenNameMINUS = 5,
							TokenNameNOT = 68,
							TokenNameREMAINDER = 9,
							TokenNameXOR = 26,
							TokenNameAND = 22,
							TokenNameMULTIPLY = 8,
							TokenNameOR = 28,
							TokenNameTWIDDLE = 69,
							TokenNameDIVIDE = 10,
							TokenNameGREATER = 15,
							TokenNameLESS = 11,
							TokenNameLPAREN = 23,
							TokenNameRPAREN = 24,
							TokenNameLBRACE = 40,
							TokenNameRBRACE = 33,
							TokenNameLBRACKET = 6,
							TokenNameRBRACKET = 71,
							TokenNameSEMICOLON = 25,
							TokenNameQUESTION = 29,
							TokenNameCOLON = 67,
							TokenNameCOMMA = 32,
							TokenNameDOT = 1,
							TokenNameEQUAL = 81,
							TokenNameAT = 36,
							TokenNameELLIPSIS = 124,
							TokenNameARROW = 108,
							TokenNameCOLON_COLON = 7,
							TokenNameBeginLambda = 63,
							TokenNameBeginIntersectionCast = 70,
							TokenNameBeginTypeArguments = 93,
							TokenNameElidedSemicolonAndRightBrace = 73,
							TokenNameAT308 = 27,
							TokenNameAT308DOTDOTDOT = 133,
							TokenNameJAVADOC_FORMAL_PART_START = 52,
							TokenNameJAVADOC_FORMAL_PART_SEPARATOR = 74,
							TokenNameJAVADOC_FORMAL_PART_END = 75,
							TokenNameold = 64,
							TokenNameBeginCaseExpr = 76,
							TokenNameRestrictedIdentifierYield = 84,
							TokenNameRestrictedIdentifierrecord = 79,
							TokenNameRestrictedIdentifiersealed = 41,
							TokenNameRestrictedIdentifierpermits = 131,
							TokenNameBeginCaseElement = 134,
							TokenNameRestrictedIdentifierWhen = 135,
							TokenNameBeginRecordPattern = 136,
							TokenNameEOF = 66,
							TokenNameERROR = 142;
}
