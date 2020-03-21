package org.eclipse.jdt.internal.compiler.ast;

import java.util.Locale;

// The fact that this class extends 'Expression' is a hack. It allows instances to be stored in the expressionStack during parsing.
public class FormalSpecificationClause extends Expression {

	public static enum Tag { PRE, POST, THROWS, MAY_THROW }

	public Tag tag;
	public Expression expression;

	public FormalSpecificationClause(Tag tag, Expression expression) {
		this.tag = tag;
		this.expression = expression;
	}

	@Override
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("/**@"); //$NON-NLS-1$
		output.append(this.tag.toString().toLowerCase(Locale.ROOT));
		output.append(' ');
		this.expression.print(0, output);
		output.append(" */"); //$NON-NLS-1$
		return output;
	}

}
