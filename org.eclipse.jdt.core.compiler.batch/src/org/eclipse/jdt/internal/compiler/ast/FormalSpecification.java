package org.eclipse.jdt.internal.compiler.ast;

import java.util.ArrayList;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.codegen.Opcodes;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeIds;

public class FormalSpecification {

	private static final char[] preconditionAssertionMessage = "Precondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] postconditionAssertionMessage = "Postcondition does not hold".toCharArray(); //$NON-NLS-1$
	private static final char[] POSTCONDITION_VARIABLE_NAME = " $post".toCharArray(); //$NON-NLS-1$
	private static final char[] POSTCONDITION_METHOD_NAME_SUFFIX = "$post".toCharArray(); //$NON-NLS-1$
	static final char[] OLD_VARIABLE_PREFIX = " old$".toCharArray(); //$NON-NLS-1$
	private static final char[] RESULT_NAME = "result".toCharArray(); //$NON-NLS-1$
	
	private static QualifiedTypeReference getTypeReference(String name) {
		String[] components = name.split("\\."); //$NON-NLS-1$
		char[][] sources = new char[components.length][];
		long[] poss = new long[components.length];
		for (int i = 0; i < components.length; i++)
			sources[i] = components[i].toCharArray();
		return new QualifiedTypeReference(sources, poss);
	}
	
	private static final QualifiedTypeReference javaLangRunnable = getTypeReference("java.lang.Runnable"); //$NON-NLS-1$
	private static final QualifiedTypeReference javaUtilFunctionIntConsumer = getTypeReference("java.util.function.IntConsumer"); //$NON-NLS-1$
	
	private static QualifiedTypeReference getPostconditionLambdaType(TypeBinding returnType) {
		switch (returnType.id) {
			case TypeIds.T_void: return javaLangRunnable;
			case TypeIds.T_int: return javaUtilFunctionIntConsumer;
			default: throw new AssertionError("Return type not yet supported: " + returnType); //$NON-NLS-1$
		}
	}

	public final AbstractMethodDeclaration method;
	public Expression[] preconditions;
	public ArrayList<OldExpression> oldExpressions;
	public Expression[] postconditions;
	
	public Block block;
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
			if (this.postconditions != null)
				for (Expression e : this.postconditions)
					e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
		} else {
			ArrayList<Statement> statementsForBlock = new ArrayList<>();
			int blockDeclarationsCount = 0;
			ArrayList<Statement> statementsForMethodBody = new ArrayList<>();
			if (this.preconditions != null) {
				// Insert assert statements into method body.
				// FIXME(fsc4j): If this is a constructor without an explicit super()/this(), a super()/this() will incorrectly be inserted *before* the asserts.
				for (int i = 0; i < this.preconditions.length; i++) {
					Expression e = this.preconditions[i];
					statementsForBlock.add(new AssertStatement(new StringLiteral(preconditionAssertionMessage, e.sourceStart, e.sourceEnd, 0), e, e.sourceStart));
				}
			}
			if (this.postconditions != null) {
				this.oldExpressions = new ArrayList<>();
				for (int i = 0; i < this.postconditions.length; i++) {
					this.postconditions[i].traverse(new ASTVisitor() {

						@Override
						public boolean visit(OldExpression oldExpression, BlockScope blockScope) {
							if (oldExpression.index != -1)
								throw new AssertionError();
							oldExpression.index = FormalSpecification.this.oldExpressions.size();
							oldExpression.declaration = new LocalDeclaration(CharOperation.concat(OLD_VARIABLE_PREFIX, String.valueOf(oldExpression.index).toCharArray()), oldExpression.sourceStart, oldExpression.sourceEnd);
							long pos = (oldExpression.sourceStart << 32) + oldExpression.sourceEnd;
							oldExpression.declaration.type = new SingleTypeReference("var".toCharArray(), pos); //$NON-NLS-1$
							oldExpression.declaration.initialization = oldExpression.expression;
							statementsForBlock.add(oldExpression.declaration);
							oldExpression.reference = new SingleNameReference(oldExpression.declaration.name, pos);
							FormalSpecification.this.oldExpressions.add(oldExpression);
							return false;
						}
						
					}, this.method.scope);
				}
				blockDeclarationsCount += this.oldExpressions.size();
				
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
				if (this.method.binding.returnType.id != TypeIds.T_void)
					postconditionLambda.setArguments(new Argument[] {new Argument(RESULT_NAME, (this.method.bodyStart << 32) + this.method.bodyStart, null, 0, true)});
				postconditionLambda.setBody(postconditionBlock);
				postconditionLambda.sourceStart = postconditionBlock.sourceStart;
				postconditionLambda.sourceEnd = postconditionBlock.sourceEnd;
				this.postconditionVariableDeclaration = new LocalDeclaration(POSTCONDITION_VARIABLE_NAME, this.method.bodyStart, this.method.bodyStart);
				this.postconditionVariableDeclaration.type = getPostconditionLambdaType(this.method.binding.returnType);
				statementsForMethodBody.add(this.postconditionVariableDeclaration);
				this.method.explicitDeclarations++;
				statementsForBlock.add(new Assignment(new SingleNameReference(this.postconditionVariableDeclaration.name, (this.method.bodyStart << 32) + this.method.bodyStart), postconditionLambda, this.method.bodyStart));
				
				this.postconditionMethodCall = new MessageSend();
				this.postconditionMethodCall.receiver = new SingleNameReference(POSTCONDITION_VARIABLE_NAME, (this.method.bodyStart<< 32) + this.method.bodyStart);
				if (this.method.binding.returnType.id == TypeIds.T_void)
					this.postconditionMethodCall.selector = "run".toCharArray(); //$NON-NLS-1$
				else {
					this.postconditionMethodCall.selector = "accept".toCharArray(); //$NON-NLS-1$
					switch (this.method.binding.returnType.id) {
						case TypeIds.T_int: this.postconditionMethodCall.arguments = new Expression[] {new IntLiteral("0".toCharArray(), "0".toCharArray(), 0, 0)}; break; //$NON-NLS-1$ //$NON-NLS-2$
						default:
							throw new AssertionError();
					}
				}
			}
			this.block = new Block(blockDeclarationsCount);
			this.block.statements = new Statement[statementsForBlock.size()];
			statementsForBlock.toArray(this.block.statements);
			statementsForMethodBody.add(this.block);
			
			Statement[] statements = this.method.statements;
			if (statements == null)
				statements = new Statement[statementsForMethodBody.size()];
			else {
				int length = statements.length;
				System.arraycopy(statements, 0, statements = new Statement[statementsForMethodBody.size() + length], statementsForMethodBody.size(), length);
			}
			for (int i = 0; i < statementsForMethodBody.size(); i++)
				statements[i] = statementsForMethodBody.get(i);
			this.method.statements = statements;
			
			if (this.preconditions != null)
				this.method.bodyStart = this.preconditions[0].sourceStart;
			else
				this.method.bodyStart = this.postconditions[0].sourceStart;
		}
	}

	public void generatePostconditionCheck(CodeStream codeStream) {
		if (this.postconditions != null) {
			int returnType = this.method.binding.returnType.id;
			if (returnType == TypeIds.T_void)
				codeStream.load(this.postconditionVariableDeclaration.binding);
			else {
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
			}
			TypeBinding constantPoolDeclaringClass = CodeStream.getConstantPoolDeclaringClass(this.method.scope, this.postconditionMethodCall.binding, this.postconditionMethodCall.binding.declaringClass, false);
			codeStream.invoke(Opcodes.OPC_invokeinterface, this.postconditionMethodCall.binding, constantPoolDeclaringClass);
		}
		
	}

}
