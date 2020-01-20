package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class OldExpression extends Expression {
	
	public Expression expression;

	public OldExpression(int sourceStart, Expression expression, int sourceEnd) {
		this.sourceStart = sourceStart;
		this.expression = expression;
		this.sourceEnd = sourceEnd;
	}

	@Override
	public TypeBinding resolveType(BlockScope scope) {
		return this.expression.resolveType(scope);
	}

	@Override
	public FlowInfo analyseCode(BlockScope currentScope, FlowContext flowContext, FlowInfo flowInfo,
			boolean valueRequired) {
		return this.expression.analyseCode(currentScope, flowContext, flowInfo, valueRequired);
	}

	@Override
	public void generateCode(BlockScope currentScope, CodeStream codeStream, boolean valueRequired) {
		this.expression.generateCode(currentScope, codeStream, valueRequired);
	}

	@Override
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("old("); //$NON-NLS-1$
		this.expression.printExpression(indent, output);
		output.append(")"); //$NON-NLS-1$
		return output;
	}

}
