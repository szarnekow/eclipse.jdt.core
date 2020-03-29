package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class OldExpression extends Expression {
	
	public static class DistinctExpression {
		LocalDeclaration exceptionDeclaration; 
		LocalDeclaration outerDeclaration;
		LocalDeclaration innerDeclaration;
		
	}
	
	/*
	 * /** @post | foo.getX() == old(foo.getX()) * /
	 * static void bar(Foo foo) { BODY }
	 * 
	 * is translated into
	 * 
	 * static void bar(Foo foo) {
	 *     Object `old(foo.getX())` = null;  // outerDeclaration
	 *     Throwable `old(foo.getX()) exception` = null;  // exceptionDeclaration
	 *     try {
	 *         var `old(foo.getX())$inner` = foo.getX();  // innerDeclaration
	 *         `old(foo.getX())` = `old(foo.getX()) inner`;
	 *     } catch (Throwable t) {
	 *         `old(foo.getX()) exception` = t;
	 *     }
	 *     BODY
	 *     assert foo.getX() == (`old(foo.getX()) exception` == null ? (T)`old(foo.getX())` : throw new AssertionError("Exception in old expression", `old(foo.getX()) exception`))
	 * }
	 * 
	 * where T is the type of `old$foo.getX()$inner`.
	 */
	public Expression expression;
	public DistinctExpression distinctExpression;
	public SingleNameReference reference;
	public SingleNameReference exceptionReference;
	public char[] source;

	public OldExpression(int sourceStart, Expression expression, int sourceEnd, char[] source) {
		this.sourceStart = sourceStart;
		this.expression = expression;
		this.sourceEnd = sourceEnd;
		this.source = new char[sourceEnd - sourceStart + 1];
		System.arraycopy(source, sourceStart, this.source, 0, sourceEnd - sourceStart + 1);
		this.constant = Constant.NotAConstant;
	}

	@Override
	public TypeBinding resolveType(BlockScope scope) {
		if (this.reference != null) {
			this.exceptionReference.resolveType(scope);
			this.reference.resolveType(scope);
			return this.resolvedType = this.distinctExpression.innerDeclaration.binding.type;
		} else
			return this.resolvedType = this.expression.resolveType(scope);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		return this.analyseCode(currentScope, flowContext, flowInfo, true);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
			boolean valueRequired) {
		if (this.reference != null) {
			flowInfo = this.exceptionReference.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
			flowInfo = this.reference.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
			return flowInfo;
		} else
			return this.expression.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
	}

	@Override
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		if (this.reference != null) {
			if (valueRequired) {
				//this.exceptionReference.generateCode(currentScope, codeStream, valueRequired);
				//codeStream.dup();
				//BranchLabel nonnullLabel = new BranchLabel(codeStream);
				//codeStream.ifnonnull(nonnullLabel);
				this.reference.generateCode(currentScope, codeStream, valueRequired);
				TypeBinding resultType = this.distinctExpression.innerDeclaration.binding.type;
				if (resultType.isBaseType()) {
					TypeBinding boxedType = currentScope.boxing(resultType);
					codeStream.checkcast(boxedType);
					codeStream.generateUnboxingConversion(resultType.id);
				} else {
					codeStream.checkcast(resultType.erasure());
				}
				codeStream.generateImplicitConversion(this.implicitConversion);
			}
		} else
			this.expression.generateCode(currentScope, codeStream, valueRequired);
	}

	@Override
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("old("); //$NON-NLS-1$
		this.expression.printExpression(indent, output);
		output.append(")"); //$NON-NLS-1$
		return output;
	}

	@Override
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			if (this.reference != null) {
				this.reference.traverse(visitor, scope);
				this.exceptionReference.traverse(visitor, scope);
			} else if (this.expression != null)
				this.expression.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}

}
