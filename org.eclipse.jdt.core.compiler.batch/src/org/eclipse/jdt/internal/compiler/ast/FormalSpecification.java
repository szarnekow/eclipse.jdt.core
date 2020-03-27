package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.flow.ExceptionHandlingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class FormalSpecification {

	private static final char[] preconditionAssertionMessage = "Precondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] postconditionAssertionMessage = "Postcondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] POSTCONDITION_VARIABLE_NAME = " $post".toCharArray(); //$NON-NLS-1$
	private static final char[] POSTCONDITION_METHOD_NAME_SUFFIX = "$post".toCharArray(); //$NON-NLS-1$
	static final char[] OLD_VARIABLE_PREFIX = "old$".toCharArray(); //$NON-NLS-1$
	private static final char[] LAMBDA_PARAMETER_NAME = " $result".toCharArray(); //$NON-NLS-1$
	private static final char[] RESULT_NAME = "result".toCharArray(); //$NON-NLS-1$
	
	private static QualifiedTypeReference getTypeReference(String name) {
		String[] components = name.split("\\."); //$NON-NLS-1$
		char[][] sources = new char[components.length][];
		long[] poss = new long[components.length];
		for (int i = 0; i < components.length; i++)
			sources[i] = components[i].toCharArray();
		return new QualifiedTypeReference(sources, poss);
	}
	
	private static QualifiedTypeReference javaLangObject() { return getTypeReference("java.lang.Object"); } //$NON-NLS-1$
	private static QualifiedTypeReference javaLangRunnable() { return getTypeReference("java.lang.Runnable"); } //$NON-NLS-1$
	private static QualifiedTypeReference javaUtilFunctionConsumer() { return getTypeReference("java.util.function.Consumer"); } //$NON-NLS-1$
	
	private static TypeReference getBoxedType(TypeBinding binding, TypeReference reference) {
		switch (binding.id) {
			case TypeIds.T_boolean: return getTypeReference("java.lang.Boolean"); //$NON-NLS-1$
			case TypeIds.T_byte: return getTypeReference("java.lang.Byte"); //$NON-NLS-1$
			case TypeIds.T_char: return getTypeReference("java.lang.Character"); //$NON-NLS-1$
			case TypeIds.T_double: return getTypeReference("java.lang.Double"); //$NON-NLS-1$
			case TypeIds.T_float: return getTypeReference("java.lang.Float"); //$NON-NLS-1$
			case TypeIds.T_int: return getTypeReference("java.lang.Integer"); //$NON-NLS-1$
			case TypeIds.T_long: return getTypeReference("java.lang.Long"); //$NON-NLS-1$
			case TypeIds.T_short: return getTypeReference("java.lang.Short"); //$NON-NLS-1$
			default: return reference;
		}
	}
	
	private static QualifiedTypeReference getJavaUtilConsumerOf(TypeReference typeArgument) {
		TypeReference[][] typeArguments = new TypeReference[][] { null, null, null, {typeArgument}};
		QualifiedTypeReference javaUtilFunctionConsumer = javaUtilFunctionConsumer();
		return new ParameterizedQualifiedTypeReference(javaUtilFunctionConsumer.tokens, typeArguments, 0, javaUtilFunctionConsumer.sourcePositions);
	}
	
	private static QualifiedTypeReference getPostconditionLambdaType(TypeBinding returnTypeBinding, TypeReference returnType) {
		if (returnType == null) // constructor
			return getJavaUtilConsumerOf(javaLangObject());
		switch (returnTypeBinding.id) {
			case TypeIds.T_void: return javaLangRunnable();
			default: return getJavaUtilConsumerOf(getBoxedType(returnTypeBinding, returnType));
		}
	}

	public final AbstractMethodDeclaration method;
	public Expression[] invariants; // Package representation invariants are specified in the Javadoc comments for the default access getters.
	public Expression[] preconditions;
	public ArrayList<OldExpression> oldExpressions;
	public Expression[] throwsConditions;
	public Expression[] mayThrowConditions;
	public Expression[] postconditions;
	
	// All of the below are null if no corresponding Javadoc tag is present; they are an empty array if an empty tag is present.
	public Expression[] inspectsExpressions;
	public Expression[] mutatesExpressions;
	public Expression[] mutatesPropertiesExpressions;
	public Expression[] createsExpressions;
	
	public Block block;
	public LocalDeclaration postconditionVariableDeclaration;
	public MessageSend postconditionMethodCall;
	public ArrayList<Statement> statementsForMethodBody;

	public FormalSpecification(AbstractMethodDeclaration method) {
		this.method = method;
	}

	public void print(int tab, StringBuffer output) {
		if (this.invariants != null) {
			for (int i = 0; i < this.invariants.length; i++) {
				output.append("/** @invar | "); //$NON-NLS-1$
				this.invariants[i].printExpression(tab, output);
				output.append(" */"); //$NON-NLS-1$
			}
		}
		if (this.preconditions != null) {
			for (int i = 0; i < this.preconditions.length; i++) {
				output.append("/** @pre | "); //$NON-NLS-1$
				this.preconditions[i].printExpression(tab, output);
				output.append(" */"); //$NON-NLS-1$
			}
		}
		if (this.throwsConditions != null) {
			for (int i = 0; i < this.throwsConditions.length; i++) {
				output.append("/** @throws | "); //$NON-NLS-1$
				this.throwsConditions[i].printExpression(tab, output);
				output.append(" */"); //$NON-NLS-1$
			}
		}
		if (this.mayThrowConditions != null) {
			for (int i = 0; i < this.mayThrowConditions.length; i++) {
				output.append("/** @may_throw | "); //$NON-NLS-1$
				this.mayThrowConditions[i].printExpression(tab, output);
				output.append(" */"); //$NON-NLS-1$
			}
		}
		if (this.postconditions != null) {
			for (int i = 0; i < this.postconditions.length; i++) {
				output.append("/** @post | "); //$NON-NLS-1$
				this.postconditions[i].printExpression(tab, output);
				output.append(" */"); //$NON-NLS-1$
			}
		}
		if (this.inspectsExpressions != null) {
			output.append("/** @inspects | "); //$NON-NLS-1$
			for (int i = 0; i < this.inspectsExpressions.length; i++) {
				if (i != 0)
					output.append(", "); //$NON-NLS-1$
				this.inspectsExpressions[i].printExpression(tab, output);
			}
			output.append(" */"); //$NON-NLS-1$
		}
		if (this.mutatesExpressions != null) {
			output.append("/** @mutates | "); //$NON-NLS-1$
			for (int i = 0; i < this.mutatesExpressions.length; i++) {
				if (i != 0)
					output.append(", "); //$NON-NLS-1$
				this.mutatesExpressions[i].printExpression(tab, output);
			}
			output.append(" */"); //$NON-NLS-1$
		}
		if (this.mutatesPropertiesExpressions != null) {
			output.append("/** @mutates_properties | "); //$NON-NLS-1$
			for (int i = 0; i < this.mutatesPropertiesExpressions.length; i++) {
				if (i != 0)
					output.append(", "); //$NON-NLS-1$
				this.mutatesPropertiesExpressions[i].printExpression(tab, output);
			}
			output.append(" */"); //$NON-NLS-1$
		}
		if (this.createsExpressions != null) {
			output.append("/** @creates | "); //$NON-NLS-1$
			for (int i = 0; i < this.createsExpressions.length; i++) {
				if (i != 0)
					output.append(", "); //$NON-NLS-1$
				this.createsExpressions[i].printExpression(tab, output);
			}
			output.append(" */"); //$NON-NLS-1$
		}
	}
	
	private void resolveEffectClause(Expression[] expressions) {
		TypeBinding javaLangObject = this.method.scope.getJavaLangObject();
		
		if (expressions != null) {
			for (Expression e : expressions) {
				if (e instanceof SpreadExpression)
					((SpreadExpression)e).body.resolveTypeExpecting(this.method.scope, javaLangObject); // TODO: Should be Iterable<?>
				else
					e.resolveTypeExpecting(this.method.scope, javaLangObject);
			}
		}
	}

	public void resolve() {
		if (this.method.ignoreFurtherInvestigation)
			return;
		
		if (this.invariants != null)
			for (Expression e : this.invariants)
				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
		if (this.throwsConditions != null)
			for (Expression e : this.throwsConditions)
				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
		if (this.mayThrowConditions != null)
			for (Expression e : this.mayThrowConditions)
				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
		
		resolveEffectClause(this.inspectsExpressions);
		resolveEffectClause(this.mutatesExpressions);
		//resolveEffectClause(this.createsExpressions); // @creates expressions can refer to 'result'
		
		if (this.mutatesPropertiesExpressions != null) {
			for (Expression e : this.mutatesPropertiesExpressions) {
				e.resolveType(this.method.scope);
				if (!(e instanceof MessageSend))
					this.method.scope.problemReporter().mutatesPropertiesExpressionShouldBeMethodCall(e);
			}
		}
		
		{
			ArrayList<Statement> statementsForBlock = new ArrayList<>();
			HashMap<String, LocalDeclaration> oldExpressions = new HashMap<>();
			int blockDeclarationsCount = 0;
			this.statementsForMethodBody = new ArrayList<>();
			if (this.preconditions != null) {
				// Insert assert statements into method body.
				// FIXME(fsc4j): If this is a constructor without an explicit super()/this(), a super()/this() will incorrectly be inserted *before* the asserts.
				for (int i = 0; i < this.preconditions.length; i++) {
					Expression e = this.preconditions[i];
					statementsForBlock.add(new AssertStatement(new StringLiteral(preconditionAssertionMessage, e.sourceStart, e.sourceEnd, 0), e, e.sourceStart));
				}
			}
			if (this.postconditions != null) {
				for (int i = 0; i < this.postconditions.length; i++) {
					this.postconditions[i].traverse(new ASTVisitor() {

						@Override
						public boolean visit(OldExpression oldExpression, BlockScope blockScope) {
							char[] name = Arrays.copyOf(oldExpression.source, oldExpression.source.length);
							for (int i = 0; i < name.length; i++) { // JVMS 4.2.2 field names cannot contain . ; [ / .
								switch (name[i]) {
									case '.': name[i] = '\u2024'; break; // ONE DOT LEADER
									case ';': name[i] = '\u204f'; break; // REVERSED SEMICOLON 
									case '[': name[i] = '\u298b'; break; // LEFT SQUARE BRACKET WITH UNDERBAR
									case ']': name[i] = '\u298c'; break; // RIGHT SQUARE BRACKET WITH UNDERBAR
									case '/': name[i] = '\u2afd'; break; // DOUBLE SOLIDUS OPERATOR
								}
							}
							String nameString = String.valueOf(name);
							LocalDeclaration declaration = oldExpressions.get(nameString);
							long pos = (oldExpression.sourceStart << 32) + oldExpression.sourceEnd;
							if (declaration == null) {
								declaration = new LocalDeclaration(name, oldExpression.sourceStart, oldExpression.sourceEnd);
								declaration.type = new SingleTypeReference("var".toCharArray(), pos); //$NON-NLS-1$
								declaration.initialization = oldExpression.expression;
								statementsForBlock.add(declaration);
								oldExpressions.put(nameString, declaration);
							}
							oldExpression.declaration = declaration;
							oldExpression.reference = new SingleNameReference(name, pos);
							return false;
						}
						
					}, this.method.scope);
				}
				blockDeclarationsCount += oldExpressions.size();
				
				ArrayList<Statement> postconditionBlockStatements = new ArrayList<>();
				int postconditionBlockDeclarationsCount = 0;
				LocalDeclaration resultDeclaration = null;
				if (this.method instanceof MethodDeclaration) {
					MethodDeclaration md = (MethodDeclaration)this.method;
					if (md.binding.returnType.id != TypeIds.T_void) {
						resultDeclaration = new LocalDeclaration(RESULT_NAME, this.method.bodyStart, this.method.bodyStart);
						resultDeclaration.type = md.returnType;
						resultDeclaration.initialization = new SingleNameReference(LAMBDA_PARAMETER_NAME, (this.method.bodyStart << 32) + this.method.bodyStart);
						postconditionBlockStatements.add(resultDeclaration);
						postconditionBlockDeclarationsCount++;
					}
				}
				for (int i = 0; i < this.postconditions.length; i++) {
					Expression e = this.postconditions[i];
					postconditionBlockStatements.add(new AssertStatement(new StringLiteral(postconditionAssertionMessage, e.sourceStart, e.sourceEnd, 0), e, e.sourceStart));
				}
				Block postconditionBlock = new Block(postconditionBlockDeclarationsCount);
				postconditionBlock.statements = new Statement[postconditionBlockStatements.size()];
				postconditionBlockStatements.toArray(postconditionBlock.statements);
				postconditionBlock.sourceStart = this.postconditions[0].sourceStart;
				postconditionBlock.sourceEnd = this.method.bodyEnd;
				LambdaExpression postconditionLambda = new LambdaExpression(this.method.compilationResult, false);
				postconditionLambda.allowReferencesToNonEffectivelyFinalOuterLocals = true;
				if (this.method instanceof ConstructorDeclaration)
					postconditionLambda.lateBindReceiver = true;
				int overloadCount = this.method.scope.enclosingClassScope().registerOverload(this.method.selector);
				postconditionLambda.lambdaMethodSelector =
						overloadCount == 0 ?
								CharOperation.concat(this.method.selector, POSTCONDITION_METHOD_NAME_SUFFIX)
						:
								CharOperation.concat(this.method.selector, ("$" + overloadCount).toCharArray(), POSTCONDITION_METHOD_NAME_SUFFIX); //$NON-NLS-1$
				if (this.method.binding.returnType.id != TypeIds.T_void || this.method instanceof ConstructorDeclaration)
					postconditionLambda.setArguments(new Argument[] {new Argument(LAMBDA_PARAMETER_NAME, (this.method.bodyStart << 32) + this.method.bodyStart, null, 0, true)});
				postconditionLambda.setBody(postconditionBlock);
				postconditionLambda.sourceStart = this.method.bodyStart;
				postconditionLambda.sourceEnd = this.method.bodyEnd;
				this.postconditionVariableDeclaration = new LocalDeclaration(POSTCONDITION_VARIABLE_NAME, this.method.bodyStart, this.method.bodyStart);
				this.postconditionVariableDeclaration.type = getPostconditionLambdaType(this.method.binding.returnType, this.method instanceof MethodDeclaration ? ((MethodDeclaration)this.method).returnType : null);
				this.statementsForMethodBody.add(this.postconditionVariableDeclaration);
				this.method.explicitDeclarations++;
				statementsForBlock.add(new Assignment(new SingleNameReference(this.postconditionVariableDeclaration.name, (this.method.bodyStart << 32) + this.method.bodyStart), postconditionLambda, this.method.bodyStart));
				
				this.postconditionMethodCall = new MessageSend();
				this.postconditionMethodCall.receiver = new SingleNameReference(POSTCONDITION_VARIABLE_NAME, (this.method.bodyStart<< 32) + this.method.bodyStart);
				if (this.method.binding.returnType.id == TypeIds.T_void && !(this.method instanceof ConstructorDeclaration))
					this.postconditionMethodCall.selector = "run".toCharArray(); //$NON-NLS-1$
				else {
					this.postconditionMethodCall.selector = "accept".toCharArray(); //$NON-NLS-1$
					this.postconditionMethodCall.arguments = new Expression[] {new NullLiteral(0, 0)};
				}
			}
			this.block = new Block(blockDeclarationsCount);
			this.block.statements = new Statement[statementsForBlock.size()];
			statementsForBlock.toArray(this.block.statements);
			this.statementsForMethodBody.add(this.block);
			
			for (Statement s : this.statementsForMethodBody)
				s.resolve(this.method.scope);
		}
		
		int thisElementModifiers = this.method.binding.modifiers;
		ReferenceBinding thisClassBinding = this.method.binding.declaringClass;
		
		if (this.invariants != null)
			for (Expression e : this.invariants)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.preconditions != null)
			for (Expression e : this.preconditions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.throwsConditions != null)
			for (Expression e : this.throwsConditions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.mayThrowConditions != null)
			for (Expression e : this.mayThrowConditions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.postconditions != null)
			for (Expression e : this.postconditions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		
		if (this.inspectsExpressions != null)
			for (Expression e : this.inspectsExpressions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.mutatesExpressions != null)
			for (Expression e : this.mutatesExpressions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		if (this.mutatesPropertiesExpressions != null)
			for (Expression e : this.mutatesPropertiesExpressions)
				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
		// TODO(fs4j): Enable once @creates clauses are typechecked
//		if (this.createsExpressions != null)
//			for (Expression e : this.createsExpressions)
//				check(thisElementModifiers, thisClassBinding, this.method.scope, e);
	}
	
	public static void check(int thisElementModifiers, ReferenceBinding thisClassBinding, BlockScope thisScope, Expression e) {
		ASTVisitor checker = new ASTVisitor() {
			
			private boolean isVisible(int modifiers, PackageBinding packageBinding) {
				if ((modifiers & ClassFileConstants.AccPublic) != 0)
					return true;
				if ((thisElementModifiers & ClassFileConstants.AccPublic) != 0 && thisClassBinding.isPublic())
					return false;
				if ((thisElementModifiers & ClassFileConstants.AccPrivate) != 0)
					return true;
				if ((modifiers & ClassFileConstants.AccPrivate) != 0)
					return false;
				// Here, both elements are either protected or package-accessible
				if (packageBinding != thisClassBinding.fPackage)
					return false;
				if ((thisElementModifiers & ClassFileConstants.AccProtected) != 0) {
					// TODO(fsc4j): More checks are required here
					if (!((modifiers & ClassFileConstants.AccProtected) != 0))
						return false;
				}
				return true;
			}
			
			private boolean isVisible(ReferenceBinding binding) {
				return binding == null || isVisible(binding.modifiers, binding.fPackage);
			}
			
			private boolean isVisible(TypeBinding binding) {
				if (binding instanceof ArrayBinding)
					return isVisible(((ArrayBinding)binding).leafComponentType);
				else if (binding instanceof ReferenceBinding)
					return isVisible((ReferenceBinding)binding);
				else
					return true;
			}

			private boolean isVisible(MethodBinding binding) {
				if (binding == null || binding.declaringClass == null)
					return true;
				if (!isVisible(binding.declaringClass))
					return false;
				return isVisible(binding.modifiers, binding.declaringClass.fPackage);
			}
			
			private void checkTypeReference(ASTNode node, TypeBinding binding) {
				if (binding != null)
					if (!isVisible(binding))
						thisScope.problemReporter().notVisibleType(node, binding);
			}
			
			private void checkConstructor(ASTNode node, MethodBinding binding) {
				if (binding != null)
					if (!isVisible(binding))
						thisScope.problemReporter().notVisibleConstructor(node, binding);
			}

			@Override
			public boolean visit(AllocationExpression allocationExpression, BlockScope scope) {
				checkConstructor(allocationExpression, allocationExpression.binding);					
				return true;
			}

			@Override
			public boolean visit(ArrayAllocationExpression arrayAllocationExpression, BlockScope scope) {
				checkTypeReference(arrayAllocationExpression, arrayAllocationExpression.resolvedType);
				return true;
			}

			@Override
			public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, BlockScope scope) {
				checkTypeReference(arrayQualifiedTypeReference, arrayQualifiedTypeReference.resolvedType);
				return true;
			}

			@Override
			public boolean visit(ArrayQualifiedTypeReference arrayQualifiedTypeReference, ClassScope scope) {
				checkTypeReference(arrayQualifiedTypeReference, arrayQualifiedTypeReference.resolvedType);
				return true;
			}

			@Override
			public boolean visit(ArrayTypeReference arrayTypeReference, BlockScope scope) {
				checkTypeReference(arrayTypeReference, arrayTypeReference.resolvedType);
				return true;
			}

			@Override
			public boolean visit(ArrayTypeReference arrayTypeReference, ClassScope scope) {
				checkTypeReference(arrayTypeReference, arrayTypeReference.resolvedType);
				return true;
			}
			
			private void checkAssignment(ASTNode node) {
				thisScope.problemReporter().assignmentInJavadoc(node);
			}

			@Override
			public boolean visit(Assignment assignment, BlockScope scope) {
				checkAssignment(assignment);
				return true;
			}

			@Override
			public boolean visit(ClassLiteralAccess classLiteral, BlockScope scope) {
				checkTypeReference(classLiteral, classLiteral.resolvedType);
				return super.visit(classLiteral, scope);
			}

			@Override
			public boolean visit(CompoundAssignment compoundAssignment, BlockScope scope) {
				checkAssignment(compoundAssignment);
				return super.visit(compoundAssignment, scope);
			}
			
			private void checkFieldReference(ASTNode node, FieldBinding binding) {
				if (binding != null && binding.declaringClass != null) // https://github.com/fsc4j/fsc4j/issues/4
					if (!isVisible(binding.declaringClass) || !isVisible(binding.modifiers, binding.declaringClass.fPackage))
						thisScope.problemReporter().notVisibleField(node, binding);
			}

			@Override
			public boolean visit(FieldReference fieldReference, BlockScope scope) {
				checkFieldReference(fieldReference, fieldReference.binding);
				return super.visit(fieldReference, scope);
			}

			@Override
			public boolean visit(FieldReference fieldReference, ClassScope scope) {
				checkFieldReference(fieldReference, fieldReference.binding);
				return super.visit(fieldReference, scope);
			}
			
			private void checkMethodReference(long nameSourcePosition, MethodBinding binding) {
				if (binding != null && binding.declaringClass != null) // https://github.com/fsc4j/fsc4j/issues/13
					if (!isVisible(binding.declaringClass) || !isVisible(binding.modifiers, binding.declaringClass.fPackage))
						thisScope.problemReporter().notVisibleMethod(nameSourcePosition, binding);
			}

			@Override
			public boolean visit(MessageSend messageSend, BlockScope scope) {
				checkMethodReference(messageSend.nameSourcePosition, messageSend.binding);
				return super.visit(messageSend, scope);
			}

			@Override
			public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference,
					BlockScope scope) {
				checkTypeReference(parameterizedQualifiedTypeReference, parameterizedQualifiedTypeReference.resolvedType);
				return super.visit(parameterizedQualifiedTypeReference, scope);
			}

			@Override
			public boolean visit(ParameterizedQualifiedTypeReference parameterizedQualifiedTypeReference,
					ClassScope scope) {
				checkTypeReference(parameterizedQualifiedTypeReference, parameterizedQualifiedTypeReference.resolvedType);
				return super.visit(parameterizedQualifiedTypeReference, scope);
			}

			@Override
			public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference,
					BlockScope scope) {
				checkTypeReference(parameterizedSingleTypeReference, parameterizedSingleTypeReference.resolvedType);
				return super.visit(parameterizedSingleTypeReference, scope);
			}

			@Override
			public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference,
					ClassScope scope) {
				checkTypeReference(parameterizedSingleTypeReference, parameterizedSingleTypeReference.resolvedType);
				return super.visit(parameterizedSingleTypeReference, scope);
			}

			@Override
			public boolean visit(PostfixExpression postfixExpression, BlockScope scope) {
				checkAssignment(postfixExpression);
				return super.visit(postfixExpression, scope);
			}

			@Override
			public boolean visit(PrefixExpression prefixExpression, BlockScope scope) {
				checkAssignment(prefixExpression);
				return super.visit(prefixExpression, scope);
			}

			@Override
			public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression, BlockScope scope) {
				checkConstructor(qualifiedAllocationExpression, qualifiedAllocationExpression.binding);
				return super.visit(qualifiedAllocationExpression, scope);
			}
			
			private void checkBinding(ASTNode node, Binding binding) {
				if (binding instanceof TypeBinding)
					checkTypeReference(node, (TypeBinding)binding);
				if (binding instanceof FieldBinding)
					checkFieldReference(node, (FieldBinding)binding);
			}
			
			private void checkQualifiedNameReference(QualifiedNameReference reference) {
				checkBinding(reference, reference.binding);
				if (reference.otherBindings != null)
					for (int i = 0; i < reference.otherBindings.length; i++)
						checkBinding(reference, reference.otherBindings[i]);
			}

			@Override
			public boolean visit(QualifiedNameReference qualifiedNameReference, BlockScope scope) {
				checkQualifiedNameReference(qualifiedNameReference);
				return super.visit(qualifiedNameReference, scope);
			}

			@Override
			public boolean visit(QualifiedNameReference qualifiedNameReference, ClassScope scope) {
				checkQualifiedNameReference(qualifiedNameReference);
				return super.visit(qualifiedNameReference, scope);
			}

			@Override
			public boolean visit(QualifiedTypeReference qualifiedTypeReference, BlockScope scope) {
				checkTypeReference(qualifiedTypeReference, qualifiedTypeReference.resolvedType);
				return super.visit(qualifiedTypeReference, scope);
			}

			@Override
			public boolean visit(QualifiedTypeReference qualifiedTypeReference, ClassScope scope) {
				checkTypeReference(qualifiedTypeReference, qualifiedTypeReference.resolvedType);
				return super.visit(qualifiedTypeReference, scope);
			}

			@Override
			public boolean visit(SingleNameReference singleNameReference, BlockScope scope) {
				checkBinding(singleNameReference, singleNameReference.binding);
				return super.visit(singleNameReference, scope);
			}

			@Override
			public boolean visit(SingleNameReference singleNameReference, ClassScope scope) {
				checkBinding(singleNameReference, singleNameReference.binding);
				return super.visit(singleNameReference, scope);
			}

			@Override
			public boolean visit(SingleTypeReference singleTypeReference, BlockScope scope) {
				checkTypeReference(singleTypeReference, singleTypeReference.resolvedType);
				return super.visit(singleTypeReference, scope);
			}

			@Override
			public boolean visit(SingleTypeReference singleTypeReference, ClassScope scope) {
				checkTypeReference(singleTypeReference, singleTypeReference.resolvedType);
				return super.visit(singleTypeReference, scope);
			}

			@Override
			public boolean visit(ThrowStatement throwStatement, BlockScope scope) {
				scope.problemReporter().throwInJavadoc(throwStatement);
				return super.visit(throwStatement, scope);
			}

			@Override
			public boolean visit(TryStatement tryStatement, BlockScope scope) {
				scope.problemReporter().tryInJavadoc(tryStatement);
				return super.visit(tryStatement, scope);
			}
			
		};
		
		e.traverse(checker, thisScope);
	}

	public void generatePostconditionCheck(CodeStream codeStream) {
		if (this.postconditions != null) {
			int returnType = this.method.binding.returnType.id;
			if (returnType == TypeIds.T_void) {
				codeStream.load(this.postconditionVariableDeclaration.binding);
				if (this.method instanceof ConstructorDeclaration)
					codeStream.aload_0();
			} else {
				if (returnType == TypeIds.T_long || returnType == TypeIds.T_double) {
					codeStream.dup2();
					codeStream.load(this.postconditionVariableDeclaration.binding);
					codeStream.dup_x2();
					codeStream.pop();
				} else {
					codeStream.dup();
					codeStream.load(this.postconditionVariableDeclaration.binding);
					codeStream.swap();
				}
				if (this.method.binding.returnType.isPrimitiveType())
					codeStream.generateBoxingConversion(returnType);
			}
			MethodBinding method = this.postconditionMethodCall.binding.original();
			TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(this.method.scope, method, method.declaringClass, false);
			codeStream.invoke(Opcodes.OPC_invokeinterface, method, constantPoolDeclaringClass);
		}
		
	}

	public FlowInfo analyseCode(MethodScope scope, ExceptionHandlingFlowContext methodContext, FlowInfo flowInfo) {
		for (Statement s : this.statementsForMethodBody)
			flowInfo = s.analyseCode(scope, methodContext, flowInfo);
		return flowInfo;
	}

	public void generateCode(MethodScope scope, CodeStream codeStream) {
		for (Statement s : this.statementsForMethodBody)
			s.generateCode(scope, codeStream);
	}

}
