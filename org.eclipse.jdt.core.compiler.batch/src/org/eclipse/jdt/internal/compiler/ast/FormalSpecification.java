package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class FormalSpecification {

	private static final char[] preconditionAssertionMessage = "Precondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] postconditionAssertionMessage = "Postcondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] POSTCONDITION_VARIABLE_NAME = " $post".toCharArray(); //$NON-NLS-1$
	private static final char[][] javaLangRunnable = {"java".toCharArray(), "lang".toCharArray(), "Runnable".toCharArray()}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final long[] javaLangRunnablePositions = {0, 0, 0};
	private static final char[] POSTCONDITION_METHOD_NAME_SUFFIX = "$post".toCharArray(); //$NON-NLS-1$

	public final AbstractMethodDeclaration method;
	public Expression[] preconditions;
	public Expression[] postconditions;
	
	public LocalDeclaration postconditionVariableDeclaration;
	public MessageSend postconditionMethodCall;

	public FormalSpecification(AbstractMethodDeclaration method) {
		this.method = method;
	}

	public void print(int tab, StringBuffer output) {
		if (this.preconditions != null) {
			for (int i = 0; i < this.preconditions.length; i++) {
				output.append("/** @pre | "); //$NON-NLS-1$
				this.preconditions[i].printExpression(tab, output);
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
	}

	public void resolve() {
		if (this.method.isAbstract() || this.method.isNative()) {
			if (this.preconditions != null)
				for (Expression e : this.preconditions)
					e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
//			if (this.postconditions != null)
//			for (Expression e : this.postconditions)
//				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
		} else {
			if (this.preconditions != null) {
				// Insert assert statements into method body.
				// FIXME(fsc4j): If this is a constructor without an explicit super()/this(), a super()/this() will incorrectly be inserted *before* the asserts.
				Statement[] statements = this.method.statements;
				if (statements == null) {
					statements = new Statement[this.preconditions.length];
				} else {
					int length = statements.length;
					System.arraycopy(statements, 0, statements = new Statement[this.preconditions.length + length], this.preconditions.length, length);
				}
				for (int i = 0; i < this.preconditions.length; i++) {
					Expression e = this.preconditions[i];
					statements[i] = new AssertStatement(new StringLiteral(preconditionAssertionMessage, e.sourceStart, e.sourceEnd, 0), e, e.sourceStart);
				}
				this.method.statements = statements;
				if (this.preconditions[0].sourceStart < this.method.bodyStart)
					this.method.bodyStart = this.preconditions[0].sourceStart;
				// The expressions will be resolved by the caller as part of method body resolution.
			}
			if (this.postconditions != null) {
				Statement[] statements = this.method.statements;
				if (statements == null) {
					statements = new Statement[1];
				} else {
					int length = statements.length;
					System.arraycopy(statements,  0,  statements = new Statement[1 + length], 1, length);
				}
				Statement[] postconditionStatements = new Statement[this.postconditions.length];
				for (int i = 0; i < this.postconditions.length; i++) {
					Expression e = this.postconditions[i];
					postconditionStatements[i] = new AssertStatement(new StringLiteral(postconditionAssertionMessage, e.sourceStart, e.sourceEnd, 0), e, e.sourceStart);
				}
				Block postconditionBlock = new Block(0);
				postconditionBlock.statements = postconditionStatements;
				postconditionBlock.sourceStart = this.postconditions[0].sourceStart;
				postconditionBlock.sourceEnd = this.postconditions[this.postconditions.length - 1].sourceEnd;
				LambdaExpression postconditionLambda = new LambdaExpression(this.method.compilationResult, false);
				postconditionLambda.lambdaMethodSelector = CharOperation.concat(this.method.selector, POSTCONDITION_METHOD_NAME_SUFFIX);
				postconditionLambda.setBody(postconditionBlock);
				postconditionLambda.sourceStart = postconditionBlock.sourceStart;
				postconditionLambda.sourceEnd = postconditionBlock.sourceEnd;
				this.postconditionVariableDeclaration = new LocalDeclaration(POSTCONDITION_VARIABLE_NAME, this.method.bodyStart, this.method.bodyStart);
				this.postconditionVariableDeclaration.type = new QualifiedTypeReference(javaLangRunnable, javaLangRunnablePositions);
				this.postconditionVariableDeclaration.initialization = postconditionLambda;
				statements[0] = this.postconditionVariableDeclaration;
				
				this.postconditionMethodCall = new MessageSend();
				this.postconditionMethodCall.receiver = new SingleNameReference(POSTCONDITION_VARIABLE_NAME, 0);
				this.postconditionMethodCall.selector = "run".toCharArray(); //$NON-NLS-1$
				
				this.method.statements = statements;
				if (this.postconditions[0].sourceStart < this.method.bodyStart)
					this.method.bodyStart = this.postconditions[0].sourceStart;
			}
		}
	}

	public void generatePostconditionCheck(CodeStream codeStream) {
		if (this.postconditions != null) {
			codeStream.load(this.postconditionVariableDeclaration.binding);
			TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(this.method.scope, this.postconditionMethodCall.binding, this.postconditionMethodCall.binding.declaringClass, false);
			codeStream.invoke(Opcodes.OPC_invokeinterface, this.postconditionMethodCall.binding, constantPoolDeclaringClass);
		}
		
	}

}
