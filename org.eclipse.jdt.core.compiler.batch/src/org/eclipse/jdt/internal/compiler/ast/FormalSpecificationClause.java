package org.eclipse.jdt.internal.compiler.ast;

import java.util.Locale;

public abstract class FormalSpecificationClause extends ASTNode {

	public static enum Tag { PRE, POST }

	public Tag tag;
	public Expression expression;

	public FormalSpecificationClause(Tag tag, Expression expression) {
		this.tag = tag;
		this.expression = expression;
	}

	@Override
	public StringBuffer print(int indent, StringBuffer output) {
		printIndent(indent, output);
		output.append("/**@"); //$NON-NLS-1$
		output.append(this.tag.toString().toLowerCase(Locale.ROOT));
		output.append(' ');
		this.expression.print(0, output);
		output.append(" */"); //$NON-NLS-1$
		return output;
	}

}
