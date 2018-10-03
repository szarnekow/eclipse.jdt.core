/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
							TokenNameCOMMENT_JAVADOC = 1003;

	int TokenNameIdentifier = 22,
							TokenNameabstract = 52,
							TokenNameassert = 77,
							TokenNameboolean = 100,
							TokenNamebreak = 75,
							TokenNamebyte = 101,
							TokenNamecase = 85,
							TokenNamecatch = 102,
							TokenNamechar = 103,
							TokenNameclass = 69,
							TokenNamecontinue = 78,
							TokenNameconst = 127,
							TokenNamedefault = 72,
							TokenNamedo = 79,
							TokenNamedouble = 104,
							TokenNameelse = 112,
							TokenNameenum = 71,
							TokenNameextends = 87,
							TokenNamefalse = 37,
							TokenNamefinal = 53,
							TokenNamefinally = 111,
							TokenNamefloat = 105,
							TokenNamefor = 80,
							TokenNamegoto = 128,
							TokenNameif = 81,
							TokenNameimplements = 123,
							TokenNameimport = 106,
							TokenNameinstanceof = 16,
							TokenNameint = 107,
							TokenNameinterface = 70,
							TokenNamelong = 108,
							TokenNamenative = 54,
							TokenNamenew = 36,
							TokenNamenull = 38,
							TokenNamepackage = 86,
							TokenNameprivate = 55,
							TokenNameprotected = 56,
							TokenNamepublic = 57,
							TokenNamereturn = 82,
							TokenNameshort = 109,
							TokenNamestatic = 49,
							TokenNamestrictfp = 58,
							TokenNamesuper = 34,
							TokenNameswitch = 39,
							TokenNamesynchronized = 51,
							TokenNamethis = 35,
							TokenNamethrow = 73,
							TokenNamethrows = 120,
							TokenNametransient = 59,
							TokenNametrue = 40,
							TokenNametry = 83,
							TokenNamevoid = 110,
							TokenNamevolatile = 60,
							TokenNamewhile = 76,
							TokenNamemodule = 113,
							TokenNameopen = 114,
							TokenNamerequires = 115,
							TokenNametransitive = 121,
							TokenNameexports = 116,
							TokenNameopens = 117,
							TokenNameto = 124,
							TokenNameuses = 118,
							TokenNameprovides = 119,
							TokenNamewith = 125,
							TokenNameIntegerLiteral = 41,
							TokenNameLongLiteral = 42,
							TokenNameFloatingPointLiteral = 43,
							TokenNameDoubleLiteral = 44,
							TokenNameCharacterLiteral = 45,
							TokenNameStringLiteral = 46,
							TokenNamePLUS_PLUS = 2,
							TokenNameMINUS_MINUS = 3,
							TokenNameEQUAL_EQUAL = 19,
							TokenNameLESS_EQUAL = 12,
							TokenNameGREATER_EQUAL = 13,
							TokenNameNOT_EQUAL = 20,
							TokenNameLEFT_SHIFT = 18,
							TokenNameRIGHT_SHIFT = 14,
							TokenNameUNSIGNED_RIGHT_SHIFT = 17,
							TokenNamePLUS_EQUAL = 88,
							TokenNameMINUS_EQUAL = 89,
							TokenNameMULTIPLY_EQUAL = 90,
							TokenNameDIVIDE_EQUAL = 91,
							TokenNameAND_EQUAL = 92,
							TokenNameOR_EQUAL = 93,
							TokenNameXOR_EQUAL = 94,
							TokenNameREMAINDER_EQUAL = 95,
							TokenNameLEFT_SHIFT_EQUAL = 96,
							TokenNameRIGHT_SHIFT_EQUAL = 97,
							TokenNameUNSIGNED_RIGHT_SHIFT_EQUAL = 98,
							TokenNameOR_OR = 31,
							TokenNameAND_AND = 30,
							TokenNamePLUS = 4,
							TokenNameMINUS = 5,
							TokenNameNOT = 63,
							TokenNameREMAINDER = 9,
							TokenNameXOR = 23,
							TokenNameAND = 21,
							TokenNameMULTIPLY = 8,
							TokenNameOR = 26,
							TokenNameTWIDDLE = 64,
							TokenNameDIVIDE = 10,
							TokenNameGREATER = 15,
							TokenNameLESS = 11,
							TokenNameLPAREN = 24,
							TokenNameRPAREN = 27,
							TokenNameLBRACE = 50,
							TokenNameRBRACE = 33,
							TokenNameLBRACKET = 6,
							TokenNameRBRACKET = 66,
							TokenNameSEMICOLON = 25,
							TokenNameQUESTION = 29,
							TokenNameCOLON = 62,
							TokenNameCOMMA = 32,
							TokenNameDOT = 1,
							TokenNameEQUAL = 74,
							TokenNameAT = 47,
							TokenNameELLIPSIS = 122,
							TokenNameARROW = 99,
							TokenNameCOLON_COLON = 7,
							TokenNameBeginLambda = 48,
							TokenNameBeginIntersectionCast = 65,
							TokenNameBeginTypeArguments = 84,
							TokenNameElidedSemicolonAndRightBrace = 68,
							TokenNameAT308 = 28,
							TokenNameAT308DOTDOTDOT = 126,
							TokenNameBeginCaseExpr = 67,
							TokenNameEOF = 61,
							TokenNameERROR = 129;
}
