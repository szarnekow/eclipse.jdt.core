package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class FormalSpecification {

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
		if (this.preconditions != null)
			for (Expression e : this.preconditions)
				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
//		if (this.postconditions != null)
//			for (Expression e : this.postconditions)
//				e.resolveTypeExpecting(this.method.scope, TypeBinding.BOOLEAN);
	}

}
