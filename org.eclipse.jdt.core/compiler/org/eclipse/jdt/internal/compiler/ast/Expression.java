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
package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.*;
import org.eclipse.jdt.internal.compiler.codegen.*;
import org.eclipse.jdt.internal.compiler.flow.*;
import org.eclipse.jdt.internal.compiler.lookup.*;
import org.eclipse.jdt.internal.compiler.problem.*;
import org.eclipse.jdt.internal.compiler.util.Messages;

public abstract class Expression extends Statement {
	
	public static final boolean isConstantValueRepresentable(
		Constant constant,
		int constantTypeID,
		int targetTypeID) {

		//true if there is no loss of precision while casting.
		// constantTypeID == constant.typeID
		if (targetTypeID == constantTypeID)
			return true;
		switch (targetTypeID) {
			case T_char :
				switch (constantTypeID) {
					case T_char :
						return true;
					case T_double :
						return constant.doubleValue() == constant.charValue();
					case T_float :
						return constant.floatValue() == constant.charValue();
					case T_int :
						return constant.intValue() == constant.charValue();
					case T_short :
						return constant.shortValue() == constant.charValue();
					case T_byte :
						return constant.byteValue() == constant.charValue();
					case T_long :
						return constant.longValue() == constant.charValue();
					default :
						return false;//boolean
				} 

			case T_float :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.floatValue();
					case T_double :
						return constant.doubleValue() == constant.floatValue();
					case T_float :
						return true;
					case T_int :
						return constant.intValue() == constant.floatValue();
					case T_short :
						return constant.shortValue() == constant.floatValue();
					case T_byte :
						return constant.byteValue() == constant.floatValue();
					case T_long :
						return constant.longValue() == constant.floatValue();
					default :
						return false;//boolean
				} 
				
			case T_double :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.doubleValue();
					case T_double :
						return true;
					case T_float :
						return constant.floatValue() == constant.doubleValue();
					case T_int :
						return constant.intValue() == constant.doubleValue();
					case T_short :
						return constant.shortValue() == constant.doubleValue();
					case T_byte :
						return constant.byteValue() == constant.doubleValue();
					case T_long :
						return constant.longValue() == constant.doubleValue();
					default :
						return false; //boolean
				} 
				
			case T_byte :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.byteValue();
					case T_double :
						return constant.doubleValue() == constant.byteValue();
					case T_float :
						return constant.floatValue() == constant.byteValue();
					case T_int :
						return constant.intValue() == constant.byteValue();
					case T_short :
						return constant.shortValue() == constant.byteValue();
					case T_byte :
						return true;
					case T_long :
						return constant.longValue() == constant.byteValue();
					default :
						return false; //boolean
				} 
				
			case T_short :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.shortValue();
					case T_double :
						return constant.doubleValue() == constant.shortValue();
					case T_float :
						return constant.floatValue() == constant.shortValue();
					case T_int :
						return constant.intValue() == constant.shortValue();
					case T_short :
						return true;
					case T_byte :
						return constant.byteValue() == constant.shortValue();
					case T_long :
						return constant.longValue() == constant.shortValue();
					default :
						return false; //boolean
				} 
				
			case T_int :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.intValue();
					case T_double :
						return constant.doubleValue() == constant.intValue();
					case T_float :
						return constant.floatValue() == constant.intValue();
					case T_int :
						return true;
					case T_short :
						return constant.shortValue() == constant.intValue();
					case T_byte :
						return constant.byteValue() == constant.intValue();
					case T_long :
						return constant.longValue() == constant.intValue();
					default :
						return false; //boolean
				} 
				
			case T_long :
				switch (constantTypeID) {
					case T_char :
						return constant.charValue() == constant.longValue();
					case T_double :
						return constant.doubleValue() == constant.longValue();
					case T_float :
						return constant.floatValue() == constant.longValue();
					case T_int :
						return constant.intValue() == constant.longValue();
					case T_short :
						return constant.shortValue() == constant.longValue();
					case T_byte :
						return constant.byteValue() == constant.longValue();
					case T_long :
						return true;
					default :
						return false; //boolean
				} 
				
			default :
				return false; //boolean
		} 
	}
	
	public Constant constant;
	
	//Some expression may not be used - from a java semantic point
	//of view only - as statements. Other may. In order to avoid the creation
	//of wrappers around expression in order to tune them as expression
	//Expression is a subclass of Statement. See the message isValidJavaStatement()

	public int implicitConversion;
	public TypeBinding resolvedType;

	public Expression() {
		super();
	}

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {

		return flowInfo;
	}

	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo, boolean valueRequired) {

		return analyseCode(currentScope, flowContext, flowInfo);
	}

	/**
	 * Returns false if cast is not legal. 
	 */
	public final boolean checkCastTypesCompatibility(
		Scope scope,
		TypeBinding castType,
		TypeBinding expressionType,
		Expression expression) {
	
		// see specifications 5.5
		// handle errors and process constant when needed
	
		// if either one of the type is null ==>
		// some error has been already reported some where ==>
		// we then do not report an obvious-cascade-error.
	
		if (castType == null || expressionType == null) return true;
	
		// identity conversion cannot be performed upfront, due to side-effects
		// like constant propagation
		LookupEnvironment env = scope.environment();
		boolean use15specifics = env.options.sourceLevel >= JDK1_5;
		if (castType.isBaseType()) {
			if (expressionType.isBaseType()) {
				if (expressionType == castType) {
					if (expression != null) {
						this.constant = expression.constant; //use the same constant
					}
					tagAsUnnecessaryCast(scope, castType);
					return true;
				}
				boolean necessary = false;
				if (expressionType.isCompatibleWith(castType)
						|| (necessary = BaseTypeBinding.isNarrowing(castType.id, expressionType.id))) {
					if (expression != null) {
						expression.implicitConversion = (castType.id << 4) + expressionType.id;
						if (expression.constant != Constant.NotAConstant) {
							constant = expression.constant.castTo(expression.implicitConversion);
						}
					}
					if (!necessary) tagAsUnnecessaryCast(scope, castType);
					return true;
					
				}
			} else if (use15specifics 
								&& env.computeBoxingType(expressionType).isCompatibleWith(castType)) { // unboxing - only widening match is allowed
				tagAsUnnecessaryCast(scope, castType);  
				return true;
			}
			return false;
		} else if (use15specifics 
							&& expressionType.isBaseType() 
							&& env.computeBoxingType(expressionType).isCompatibleWith(castType)) { // boxing - only widening match is allowed
			tagAsUnnecessaryCast(scope, castType);  
			return true;
		}
	
		//-----------cast to something which is NOT a base type--------------------------	
		if (expressionType == NullBinding) {
			tagAsUnnecessaryCast(scope, castType);
			return true; //null is compatible with every thing
		}
		if (expressionType.isBaseType()) {
			return false;
		}
	
		if (expressionType.isArrayType()) {
			if (castType == expressionType) {
				tagAsUnnecessaryCast(scope, castType);
				return true; // identity conversion
			}
	
			if (castType.isArrayType()) {
				//------- (castType.isArray) expressionType.isArray -----------
				TypeBinding castElementType = ((ArrayBinding) castType).elementsType();
				TypeBinding exprElementType = ((ArrayBinding) expressionType).elementsType();
				if (exprElementType.isBaseType() || castElementType.isBaseType()) {
					// <---stop the recursion------- 
					if (castElementType == exprElementType) {
						tagAsNeedCheckCast();
						return true;
					} else {
						return false;
					}
				}
				// recursively on the elements...
				return checkCastTypesCompatibility(scope, ((ArrayBinding) castType).elementsType(), exprElementType, expression);
			} else if (castType.isTypeVariable()) {
				if (expressionType instanceof ReferenceBinding) {
					ReferenceBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType);
					if (match == null) {
						checkUnsafeCast(scope, castType, expressionType, match, true);
					}
				} else {
					checkUnsafeCast(scope, castType, expressionType, null, true);
				}
				// recursively on the type variable upper bound
				return checkCastTypesCompatibility(scope, castType.erasure(), expressionType, expression);
			} else if (castType.isClass() || castType.isEnum()) {
				//------(castType.isClass) expressionType.isArray ---------------	
				if (castType.id == T_JavaLangObject) {
					tagAsUnnecessaryCast(scope, castType);
					return true;
				}
			} else { //------- (castType.isInterface) expressionType.isArray -----------
				if (castType.id == T_JavaLangCloneable || castType.id == T_JavaIoSerializable) {
					tagAsNeedCheckCast();
					return true;
				}
			}
			return false;
		}
		if (expressionType.isTypeVariable() || expressionType.isWildcard()) {
			if (castType instanceof ReferenceBinding) {
				TypeBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType);
				if (match != null) {
					tagAsUnnecessaryCast(scope, castType);
					return true;
				}
			}
			// recursively on the type variable upper bound
			return checkCastTypesCompatibility(scope, castType, expressionType.erasure(), expression);
		}
		
		if (expressionType.isClass() || expressionType.isEnum()) {
			if (castType.isArrayType()) {
				// ---- (castType.isArray) expressionType.isClass -------
				if (expressionType.id == T_JavaLangObject) { // potential runtime error
					tagAsNeedCheckCast();
					return true;
				}
			} else if (castType.isTypeVariable()) {
				TypeBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType);
				if (match == null) {
					checkUnsafeCast(scope, castType, expressionType, match, true);
				}
				// recursively on the type variable upper bound
				return checkCastTypesCompatibility(scope, castType.erasure(), expressionType, expression);
			} else if (castType.isClass() || castType.isEnum()) { // ----- (castType.isClass) expressionType.isClass ------
				TypeBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType.erasure());
				if (match != null) {
					if (expression != null && castType.id == T_JavaLangString) this.constant = expression.constant; // (String) cst is still a constant
					return checkUnsafeCast(scope, castType, expressionType, match, false);
				}
				match = ((ReferenceBinding)castType).findSuperTypeErasingTo((ReferenceBinding)expressionType.erasure());
				if (match != null) {
					tagAsNeedCheckCast();
					return checkUnsafeCast(scope, castType, expressionType, match, true);
				}
			} else { // ----- (castType.isInterface) expressionType.isClass -------  

				TypeBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType.erasure());
				if (match != null) {
					return checkUnsafeCast(scope, castType, expressionType, match, false);
				}
				// a subclass may implement the interface ==> no check at compile time
				if (!((ReferenceBinding) expressionType).isFinal()) {
					tagAsNeedCheckCast();
					match = ((ReferenceBinding)castType).findSuperTypeErasingTo((ReferenceBinding)expressionType.erasure());
					if (match != null) {
						return checkUnsafeCast(scope, castType, expressionType, match, true);
					}
					return true;
				}
				// no subclass for expressionType, thus compile-time check is valid
			}
			return false;
		}
	
		//	if (expressionType.isInterface()) { cannot be anything else
		if (castType.isArrayType()) {
			// ----- (castType.isArray) expressionType.isInterface ------
			if (expressionType.id == T_JavaLangCloneable
					|| expressionType.id == T_JavaIoSerializable) {// potential runtime error
				tagAsNeedCheckCast();
				return true;
			} else {
				return false;
			}
		} else if (castType.isTypeVariable()) {
			TypeBinding match = ((ReferenceBinding)expressionType).findSuperTypeErasingTo((ReferenceBinding)castType);
			if (match == null) {
				checkUnsafeCast(scope, castType, expressionType, match, true);
			}
			// recursively on the type variable upper bound
			return checkCastTypesCompatibility(scope, castType.erasure(), expressionType, expression);
		} else if (castType.isClass() || castType.isEnum()) { // ----- (castType.isClass) expressionType.isInterface --------

			if (castType.id == T_JavaLangObject) { // no runtime error
				tagAsUnnecessaryCast(scope, castType);
				return true;
			}
			if (((ReferenceBinding) castType).isFinal()) {
				// no subclass for castType, thus compile-time check is valid
				TypeBinding match = ((ReferenceBinding)castType).findSuperTypeErasingTo((ReferenceBinding)expressionType.erasure());
				if (match == null) {
					// potential runtime error
					return false;
				}				
			}
		} else { // ----- (castType.isInterface) expressionType.isInterface -------
				ReferenceBinding interfaceType = (ReferenceBinding) expressionType;
				TypeBinding match = interfaceType.findSuperTypeErasingTo((ReferenceBinding)castType.erasure());
				if (match != null) {
					return checkUnsafeCast(scope, castType, interfaceType, match, false);
				}
				
				tagAsNeedCheckCast();
				match = ((ReferenceBinding)castType).findSuperTypeErasingTo((ReferenceBinding)interfaceType.erasure());
				if (match != null) {
					return checkUnsafeCast(scope, castType, interfaceType, match, true);
				}
				if (use15specifics) {
					// a subclass may implement the interface ==> no check at compile time
					return true;
				}
				// pre1.5 semantics - no covariance allowed (even if 1.5 compliant, but 1.4 source)
				MethodBinding[] castTypeMethods = getAllInheritedMethods((ReferenceBinding) castType);
				MethodBinding[] expressionTypeMethods = getAllInheritedMethods((ReferenceBinding) expressionType);
				int exprMethodsLength = expressionTypeMethods.length;
				for (int i = 0, castMethodsLength = castTypeMethods.length; i < castMethodsLength; i++)
					for (int j = 0; j < exprMethodsLength; j++) {
						if ((castTypeMethods[i].returnType != expressionTypeMethods[j].returnType)
								&& (CharOperation.equals(castTypeMethods[i].selector, expressionTypeMethods[j].selector))
								&& castTypeMethods[i].areParametersEqual(expressionTypeMethods[j])) {
							return false;

						}
					}
				return true;
		}
		tagAsNeedCheckCast();
		return true;
	}	
	
	public FlowInfo checkNullStatus(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, int nullStatus) {

		LocalVariableBinding local = this.localVariableBinding();
		if (local != null) {
			switch(nullStatus) {
				case FlowInfo.NULL :
					flowContext.recordUsingNullReference(scope, local, this, FlowInfo.NULL, flowInfo);
					flowInfo.markAsDefinitelyNull(local); // from thereon it is set
					break;
				case FlowInfo.NON_NULL :
					flowContext.recordUsingNullReference(scope, local, this, FlowInfo.NON_NULL, flowInfo);
					flowInfo.markAsDefinitelyNonNull(local); // from thereon it is set
					break;
				case FlowInfo.UNKNOWN :
					break;
			}
		}
		return flowInfo;
	}

	private MethodBinding[] getAllInheritedMethods(ReferenceBinding binding) {
		ArrayList collector = new ArrayList();
		getAllInheritedMethods0(binding, collector);
		return (MethodBinding[]) collector.toArray(new MethodBinding[collector.size()]);
	}
	
	private void getAllInheritedMethods0(ReferenceBinding binding, ArrayList collector) {
		if (!binding.isInterface()) return;
		MethodBinding[] methodBindings = binding.methods();
		for (int i = 0, max = methodBindings.length; i < max; i++) {
			collector.add(methodBindings[i]);
		}
		ReferenceBinding[] superInterfaces = binding.superInterfaces();
		for (int i = 0, max = superInterfaces.length; i < max; i++) {
			getAllInheritedMethods0(superInterfaces[i], collector);
		}
	}
	public void checkNullComparison(BlockScope scope, FlowContext flowContext, FlowInfo flowInfo, FlowInfo initsWhenTrue, FlowInfo initsWhenFalse) {
		// do nothing by default - see EqualExpression
	}

	public boolean checkUnsafeCast(Scope scope, TypeBinding castType, TypeBinding expressionType, TypeBinding match, boolean isNarrowing) {
		if (match == castType) {
			if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
			return true;
		}
		if (match != null && (castType.isBoundParameterizedType() || castType.isGenericType() || expressionType.isBoundParameterizedType() || expressionType.isGenericType())) {
			if (match.isProvablyDistinctFrom(isNarrowing ? expressionType : castType, 0)) {
				return false; 
			}
		}
		if (!isNarrowing) tagAsUnnecessaryCast(scope, castType);
		return true;
	}
	
	/**
	 * Base types need that the widening is explicitly done by the compiler using some bytecode like i2f.
	 * Also check unsafe type operations.
	 */ 
	public void computeConversion(Scope scope, TypeBinding runtimeTimeType, TypeBinding compileTimeType) {

		if (runtimeTimeType == null || compileTimeType == null)
			return;
		if (this.implicitConversion != 0) return; // already set independantly

		// it is possible for a Byte to be unboxed to a byte & then converted to an int
		// but it is not possible for a byte to become Byte & then assigned to an Integer,
		// or to become an int before boxed into an Integer
		if (runtimeTimeType != NullBinding && runtimeTimeType.isBaseType()) {
			if (!compileTimeType.isBaseType()) {
				TypeBinding unboxedType = scope.environment().computeBoxingType(compileTimeType);
				this.implicitConversion = UNBOXING;
				scope.problemReporter().autoboxing(this, compileTimeType, runtimeTimeType);
				compileTimeType = unboxedType;
			}
		} else {
			if (compileTimeType != NullBinding && compileTimeType.isBaseType()) {
				TypeBinding boxedType = scope.environment().computeBoxingType(runtimeTimeType);
				if (boxedType == runtimeTimeType) // Object o = 12;
					boxedType = compileTimeType; 
				this.implicitConversion = BOXING | (boxedType.id << 4) + compileTimeType.id;
				scope.problemReporter().autoboxing(this, compileTimeType, scope.environment().computeBoxingType(boxedType));
				return;
			}
		}

		switch (runtimeTimeType.id) {
			case T_byte :
			case T_short :
			case T_char :
				this.implicitConversion |= (T_int << 4) + compileTimeType.id;
				break;
			case T_JavaLangString :
			case T_float :
			case T_boolean :
			case T_double :
			case T_int : //implicitConversion may result in i2i which will result in NO code gen
			case T_long :
				this.implicitConversion |= (runtimeTimeType.id << 4) + compileTimeType.id;
				break;
			default : // regular object ref
//				if (compileTimeType.isRawType() && runtimeTimeType.isBoundParameterizedType()) {
//				    scope.problemReporter().unsafeRawExpression(this, compileTimeType, runtimeTimeType);
//				}		
		}
	}	
	/**
	 * Expression statements are plain expressions, however they generate like
	 * normal expressions with no value required.
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream 
	 */
	public void generateCode(BlockScope currentScope, CodeStream codeStream) {

		if ((bits & IsReachableMASK) == 0) {
			return;
		}
		generateCode(currentScope, codeStream, false);
	}

	/**
	 * Every expression is responsible for generating its implicit conversion when necessary.
	 *
	 * @param currentScope org.eclipse.jdt.internal.compiler.lookup.BlockScope
	 * @param codeStream org.eclipse.jdt.internal.compiler.codegen.CodeStream
	 * @param valueRequired boolean
	 */
	public void generateCode(
		BlockScope currentScope,
		CodeStream codeStream,
		boolean valueRequired) {

		if (constant != NotAConstant) {
			// generate a constant expression
			int pc = codeStream.position;
			codeStream.generateConstant(constant, implicitConversion);
			codeStream.recordPositionsFrom(pc, this.sourceStart);
		} else {
			// actual non-constant code generation
			throw new ShouldNotImplement(Messages.ast_missingCode);
		}
	}

	/**
	 * Default generation of a boolean value
	 * @param currentScope
	 * @param codeStream
	 * @param trueLabel
	 * @param falseLabel
	 * @param valueRequired
	 */
	public void generateOptimizedBoolean(
		BlockScope currentScope,
		CodeStream codeStream,
		Label trueLabel,
		Label falseLabel,
		boolean valueRequired) {

		// a label valued to nil means: by default we fall through the case... 
		// both nil means we leave the value on the stack

		if ((constant != Constant.NotAConstant) && (constant.typeID() == T_boolean)) {
			int pc = codeStream.position;
			if (constant.booleanValue() == true) {
				// constant == true
				if (valueRequired) {
					if (falseLabel == null) {
						// implicit falling through the FALSE case
						if (trueLabel != null) {
							codeStream.goto_(trueLabel);
						}
					}
				}
			} else {
				if (valueRequired) {
					if (falseLabel != null) {
						// implicit falling through the TRUE case
						if (trueLabel == null) {
							codeStream.goto_(falseLabel);
						}
					}
				}
			}
			codeStream.recordPositionsFrom(pc, this.sourceStart);
			return;
		}
		generateCode(currentScope, codeStream, valueRequired);
		// branching
		int position = codeStream.position;
		if (valueRequired) {
			if (falseLabel == null) {
				if (trueLabel != null) {
					// Implicit falling through the FALSE case
					codeStream.ifne(trueLabel);
				}
			} else {
				if (trueLabel == null) {
					// Implicit falling through the TRUE case
					codeStream.ifeq(falseLabel);
				} else {
					// No implicit fall through TRUE/FALSE --> should never occur
				}
			}
		}
		// reposition the endPC
		codeStream.updateLastRecordedEndPC(currentScope, position);
	}

	/* Optimized (java) code generation for string concatenations that involve StringBuffer
	 * creation: going through this path means that there is no need for a new StringBuffer
	 * creation, further operands should rather be only appended to the current one.
	 * By default: no optimization.
	 */
	public void generateOptimizedStringConcatenation(
		BlockScope blockScope,
		CodeStream codeStream,
		int typeID) {

		if (typeID == T_JavaLangString && this.constant != NotAConstant && this.constant.stringValue().length() == 0) {
			return; // optimize str + ""
		}
		generateCode(blockScope, codeStream, true);
		codeStream.invokeStringConcatenationAppendForType(typeID);
	}

	/* Optimized (java) code generation for string concatenations that involve StringBuffer
	 * creation: going through this path means that there is no need for a new StringBuffer
	 * creation, further operands should rather be only appended to the current one.
	 */
	public void generateOptimizedStringConcatenationCreation(
		BlockScope blockScope,
		CodeStream codeStream,
		int typeID) {

		codeStream.newStringContatenation();
		codeStream.dup();
		switch (typeID) {
			case T_JavaLangObject :
			case T_undefined :
				// in the case the runtime value of valueOf(Object) returns null, we have to use append(Object) instead of directly valueOf(Object)
				// append(Object) returns append(valueOf(Object)), which means that the null case is handled by the next case.
				codeStream.invokeStringConcatenationDefaultConstructor();
				generateCode(blockScope, codeStream, true);
				codeStream.invokeStringConcatenationAppendForType(T_JavaLangObject);
				return;
			case T_JavaLangString :
			case T_null :
				if (constant != NotAConstant) {
					String stringValue = constant.stringValue();
					if (stringValue.length() == 0) {  // optimize ""+<str> 
						codeStream.invokeStringConcatenationDefaultConstructor();
						return;
					}
					codeStream.ldc(stringValue);
				} else {
					// null case is not a constant
					generateCode(blockScope, codeStream, true);
					codeStream.invokeStringValueOf(T_JavaLangObject);
				}
				break;
			default :
				generateCode(blockScope, codeStream, true);
				codeStream.invokeStringValueOf(typeID);
		}
		codeStream.invokeStringConcatenationStringConstructor();
	}

	public boolean isCompactableOperation() {

		return false;
	}

	//Return true if the conversion is done AUTOMATICALLY by the vm
	//while the javaVM is an int based-machine, thus for example pushing
	//a byte onto the stack , will automatically create an int on the stack
	//(this request some work d be done by the VM on signed numbers)
	public boolean isConstantValueOfTypeAssignableToType(TypeBinding constantType, TypeBinding targetType) {

		if (constant == Constant.NotAConstant)
			return false;
		if (constantType == targetType)
			return true;
		if (constantType.isBaseType() && targetType.isBaseType()) {
			//No free assignment conversion from anything but to integral ones.
			if ((constantType == IntBinding
				|| BaseTypeBinding.isWidening(T_int, constantType.id))
				&& (BaseTypeBinding.isNarrowing(targetType.id, T_int))) {
				//use current explicit conversion in order to get some new value to compare with current one
				return isConstantValueRepresentable(constant, constantType.id, targetType.id);
			}
		}
		return false;
	}

	public boolean isTypeReference() {
		return false;
	}
	
	public int nullStatus(FlowInfo flowInfo) {
		
		if (this.constant != null && this.constant != NotAConstant)
			return FlowInfo.NON_NULL; // constant expression cannot be null
		
		LocalVariableBinding local = localVariableBinding();
		if (local != null) {
			if (flowInfo.isDefinitelyNull(local))
				return FlowInfo.NULL;
			if (flowInfo.isDefinitelyNonNull(local))
				return FlowInfo.NON_NULL;
			return FlowInfo.UNKNOWN;
		}
		return FlowInfo.NON_NULL;
	}
	
	/**
	 * Constant usable for bytecode pattern optimizations, but cannot be inlined
	 * since it is not strictly equivalent to the definition of constant expressions.
	 * In particular, some side-effects may be required to occur (only the end value
	 * is known).
	 * @return Constant known to be of boolean type
	 */ 
	public Constant optimizedBooleanConstant() {
		return this.constant;
	}

	public StringBuffer print(int indent, StringBuffer output) {
		printIndent(indent, output);
		return printExpression(indent, output);
	}

	public abstract StringBuffer printExpression(int indent, StringBuffer output);
	
	public StringBuffer printStatement(int indent, StringBuffer output) {
		return print(indent, output).append(";"); //$NON-NLS-1$
	}

	public void resolve(BlockScope scope) {
		// drops the returning expression's type whatever the type is.

		this.resolveType(scope);
		return;
	}

	public TypeBinding resolveType(BlockScope scope) {
		// by default... subclasses should implement a better TC if required.

		return null;
	}

	public TypeBinding resolveType(ClassScope classScope) {
		// by default... subclasses should implement a better TB if required.
		return null;
	}

	public TypeBinding resolveTypeExpecting(
		BlockScope scope,
		TypeBinding expectedType) {

		this.setExpectedType(expectedType); // needed in case of generic method invocation
		TypeBinding expressionType = this.resolveType(scope);
		if (expressionType == null) return null;
		if (expressionType == expectedType) return expressionType;
		
		if (!expressionType.isCompatibleWith(expectedType)) {
			if (scope.isBoxingCompatibleWith(expressionType, expectedType)) {
				this.computeConversion(scope, expectedType, expressionType);
			} else {
				scope.problemReporter().typeMismatchError(expressionType, expectedType, this);
				return null;
			}
		}
		return expressionType;
	}

	/**
	 * Record the type expectation before this expression is typechecked.
	 * e.g. String s = foo();, foo() will be tagged as being expected of type String
	 * Used to trigger proper inference of generic method invocations.
	 */
	public void setExpectedType(TypeBinding expectedType) {
	    // do nothing by default
	}

	public void tagAsUnnecessaryCast(Scope scope, TypeBinding castType) {
	    // do nothing by default
	}
	
	public void tagAsNeedCheckCast() {
	    // do nothing by default		
	}
	
	public Expression toTypeReference() {
		//by default undefined

		//this method is meanly used by the parser in order to transform
		//an expression that is used as a type reference in a cast ....
		//--appreciate the fact that castExpression and ExpressionWithParenthesis
		//--starts with the same pattern.....

		return this;
	}
	
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		// do nothing by default
	}
	public void traverse(ASTVisitor visitor, ClassScope scope) {
		// do nothing by default
	}
	public void traverse(ASTVisitor visitor, CompilationUnitScope scope) {
		// do nothing by default
	}
	/**
	 * Returns the local variable referenced by this node. Can be a direct reference (SingleNameReference)
	 * or thru a cast expression etc...
	 */
	public LocalVariableBinding localVariableBinding() {
		return null;
	}
}
