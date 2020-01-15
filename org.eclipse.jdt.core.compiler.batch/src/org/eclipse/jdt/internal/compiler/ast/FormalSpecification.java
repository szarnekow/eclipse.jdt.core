package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class FormalSpecification {

	private static final char[] preconditionAssertionMessage = "Precondition does not hold".toCharArray(); //$NON-NLS-1$

	public final AbstractMethodDeclaration method;
	public Expression[] preconditions;
	public Expression[] postconditions;

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
				Statement[] statements = this.method.statements;
				if (statements == null) {
					statements = new Statement[this.preconditions.length];
				} else {
					int length = statements.length;
					System.arraycopy(statements, 0, statements = new Statement[this.preconditions.length + length], this.preconditions.length, length);
				}
				for (int i = 0; i < this.preconditions.length; i++) {
					statements[i] = new AssertStatement(new StringLiteral(preconditionAssertionMessage, 0, 0, 0), this.preconditions[i], 0);
				}
				this.method.statements = statements;
				// The expressions will be resolved by the caller as part of method body resolution.
			}
		}
	}

}
