package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class OldExpression extends Expression {
	
	public Expression expression;
	public int index = -1;
	public LocalDeclaration declaration;
	public SingleNameReference reference;

	public OldExpression(int sourceStart, Expression expression, int sourceEnd) {
		this.sourceStart = sourceStart;
		this.expression = expression;
		this.sourceEnd = sourceEnd;
		this.constant = Constant.NotAConstant;
	}

	@Override
	public TypeBinding resolveType(BlockScope scope) {
		if (this.reference != null)
			return this.reference.resolveType(scope);
		else
			return this.expression.resolveType(scope);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		return this.analyseCode(currentScope, flowContext, flowInfo, true);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
			boolean valueRequired) {
		if (this.reference != null)
			return this.reference.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
		else
			return this.expression.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
	}

	@Override
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		if (this.reference != null)
			this.reference.generateCode(currentScope, codeStream, valueRequired);
		else
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
			if (this.reference != null)
				this.reference.traverse(visitor, scope);
			else if (this.expression != null)
				this.expression.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}

}
