package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class SpreadExpression extends Expression {
	
	public Expression body;
	
	public SpreadExpression(int sourceStart, Expression body) {
		this.body = body;
		this.sourceStart = sourceStart;
		this.sourceEnd = body.sourceEnd;
	}
	
	@Override
	public TypeBinding resolveType(BlockScope scope) {
		scope.problemReporter().spreadExpressionNotAllowedHere(this);
		return this.resolvedType = this.body.resolveType(scope);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo) {
		return this.analyseCode(currentScope, flowContext, flowInfo, true);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
			boolean valueRequired) {
		return this.body.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
	}

	@Override
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		this.body.generateCode(currentScope, codeStream, valueRequired);
	}

	@Override
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("..."); //$NON-NLS-1$
		this.body.printExpression(indent, output);
		output.append(")"); //$NON-NLS-1$
		return output;
	}

	@Override
	public void traverse(ASTVisitor visitor, BlockScope scope) {
		if (visitor.visit(this, scope)) {
			this.body.traverse(visitor, scope);
		}
		visitor.endVisit(this, scope);
	}

}
