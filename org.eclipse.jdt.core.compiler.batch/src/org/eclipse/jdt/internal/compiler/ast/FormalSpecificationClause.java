package org.eclipse.jdt.internal.compiler.ast;

import java.util.List;
import java.util.Locale;

import org.eclipse.jdt.internal.compiler.parser.Parser;

// The fact that this class extends 'Expression' is a hack. It allows instances to be stored in the expressionStack during parsing.
public class FormalSpecificationClause extends Expression {

	public static enum Tag { PRE, POST, THROWS, MAY_THROW, INVAR, INSPECTS, MUTATES, MUTATES_PROPERTIES, CREATES }

	public int tagStart;
	public int tagEnd;
	public Tag tag;
	public TypeReference tagArgument;
	public Expression[] expressions;

	public FormalSpecificationClause(int tagStart, int tagEnd, Tag tag, TypeReference tagArgument, Expression[] expressions) {
		this.tagStart = tagStart;
		this.tagEnd = tagEnd;
		this.tag = tag;
		this.tagArgument = tagArgument;
		this.expressions = expressions;
		this.sourceStart = this.tagStart;
		this.sourceEnd = this.tagEnd;
	}
	
	public void addExpression(Parser parser, List<Expression> list) {
		if (this.expressions.length == 0)
			parser.problemReporter().expressionExpectedInJavadocFormalPart(this);
		else if (this.expressions.length > 1)
			parser.problemReporter().singleExpressionExpectedInJavadocFormalPart(this);
		for (Expression e : this.expressions)
			list.add(e);
	}

	@Override
	public StringBuffer printExpression(int indent, StringBuffer output) {
		output.append("/**@"); //$NON-NLS-1$
		output.append(this.tag.toString().toLowerCase(Locale.ROOT));
		output.append(' ');
		boolean first = true;
		for (Expression e : this.expressions) {
			if (first)
				first = false;
			else
				output.append(", "); //$NON-NLS-1$
			e.print(0, output);
		}
		output.append(" */"); //$NON-NLS-1$
		return output;
	}

}
