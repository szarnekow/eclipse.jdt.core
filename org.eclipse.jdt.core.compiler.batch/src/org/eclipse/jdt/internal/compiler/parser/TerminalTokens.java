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
							TokenNameassert = 84,
							TokenNameboolean = 108,
							TokenNamebreak = 85,
							TokenNamebyte = 109,
							TokenNamecase = 94,
							TokenNamecatch = 110,
							TokenNamechar = 111,
							TokenNameclass = 71,
							TokenNamecontinue = 86,
							TokenNameconst = 139,
							TokenNamedefault = 79,
							TokenNamedo = 87,
							TokenNamedouble = 112,
							TokenNameelse = 124,
							TokenNameenum = 77,
							TokenNameextends = 95,
							TokenNamefalse = 53,
							TokenNamefinal = 43,
							TokenNamefinally = 119,
							TokenNamefloat = 113,
							TokenNamefor = 88,
							TokenNamegoto = 140,
							TokenNameif = 89,
							TokenNameimplements = 136,
							TokenNameimport = 114,
							TokenNameinstanceof = 17,
							TokenNameint = 115,
							TokenNameinterface = 76,
							TokenNamelong = 116,
							TokenNamenative = 44,
							TokenNamenew = 38,
							TokenNamenon_sealed = 45,
							TokenNamenull = 54,
							TokenNamepackage = 93,
							TokenNameprivate = 46,
							TokenNameprotected = 47,
							TokenNamepublic = 48,
							TokenNamereturn = 90,
							TokenNameshort = 117,
							TokenNamestatic = 37,
							TokenNamestrictfp = 49,
							TokenNamesuper = 34,
							TokenNameswitch = 64,
							TokenNamesynchronized = 39,
							TokenNamethis = 35,
							TokenNamethrow = 81,
							TokenNamethrows = 120,
							TokenNametransient = 50,
							TokenNametrue = 55,
							TokenNametry = 91,
							TokenNamevoid = 118,
							TokenNamevolatile = 51,
							TokenNamewhile = 82,
							TokenNamemodule = 121,
							TokenNameopen = 122,
							TokenNamerequires = 125,
							TokenNametransitive = 131,
							TokenNameexports = 126,
							TokenNameopens = 127,
							TokenNameto = 137,
							TokenNameuses = 128,
							TokenNameprovides = 129,
							TokenNamewith = 138,
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
							TokenNamePLUS_EQUAL = 96,
							TokenNameMINUS_EQUAL = 97,
							TokenNameMULTIPLY_EQUAL = 98,
							TokenNameDIVIDE_EQUAL = 99,
							TokenNameAND_EQUAL = 100,
							TokenNameOR_EQUAL = 101,
							TokenNameXOR_EQUAL = 102,
							TokenNameREMAINDER_EQUAL = 103,
							TokenNameLEFT_SHIFT_EQUAL = 104,
							TokenNameRIGHT_SHIFT_EQUAL = 105,
							TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL = 106,
							TokenNameOR_OR = 31,
							TokenNameAND_AND = 30,
							TokenNamePLUS = 4,
							TokenNameMINUS = 5,
							TokenNameNOT = 67,
							TokenNameREMAINDER = 9,
							TokenNameXOR = 25,
							TokenNameAND = 22,
							TokenNameMULTIPLY = 8,
							TokenNameOR = 28,
							TokenNameTWIDDLE = 68,
							TokenNameDIVIDE = 10,
							TokenNameGREATER = 15,
							TokenNameLESS = 11,
							TokenNameLPAREN = 23,
							TokenNameRPAREN = 26,
							TokenNameLBRACE = 40,
							TokenNameRBRACE = 33,
							TokenNameLBRACKET = 6,
							TokenNameRBRACKET = 70,
							TokenNameSEMICOLON = 24,
							TokenNameQUESTION = 29,
							TokenNameCOLON = 66,
							TokenNameCOMMA = 32,
							TokenNameDOT = 1,
							TokenNameEQUAL = 80,
							TokenNameAT = 36,
							TokenNameELLIPSIS = 123,
							TokenNameARROW = 107,
							TokenNameCOLON_COLON = 7,
							TokenNameBeginLambda = 63,
							TokenNameBeginIntersectionCast = 69,
							TokenNameBeginTypeArguments = 92,
							TokenNameElidedSemicolonAndRightBrace = 72,
							TokenNameAT308 = 27,
							TokenNameAT308DOTDOTDOT = 132,
							TokenNameJAVADOC_FORMAL_PART_START = 52,
							TokenNameJAVADOC_FORMAL_PART_SEPARATOR = 73,
							TokenNameJAVADOC_FORMAL_PART_END = 74,
							TokenNameBeginCaseExpr = 75,
							TokenNameRestrictedIdentifierYield = 83,
							TokenNameRestrictedIdentifierrecord = 78,
							TokenNameRestrictedIdentifiersealed = 41,
							TokenNameRestrictedIdentifierpermits = 130,
							TokenNameBeginCaseElement = 133,
							TokenNameRestrictedIdentifierWhen = 134,
							TokenNameBeginRecordPattern = 135,
							TokenNameEOF = 65,
							TokenNameERROR = 141;
}
