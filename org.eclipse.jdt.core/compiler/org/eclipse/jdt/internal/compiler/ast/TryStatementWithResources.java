/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * This is an implementation of an early-draft specification developed under the Java
 * Community Process (JCP) and is made available for testing and evaluation purposes
 * only. The code is not compatible with any specification of the JCP.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

public class TryStatementWithResources extends TryStatement {

	public LocalDeclaration[] resources;
	
	public StringBuffer printStatement(int indent, StringBuffer output) {
		printIndent(indent, output).append("try ("); //$NON-NLS-1$
		int length = this.resources.length;
		for (int i = 0; i < length; i++) {
			this.resources[i].printAsExpression(0, output);
			if (i != length - 1) {
				output.append("; "); //$NON-NLS-1$
			}
		}
		output.append(")\n"); //$NON-NLS-1$
		this.tryBlock.printStatement(indent + 1, output);

		//catches
		if (this.catchBlocks != null)
			for (int i = 0; i < this.catchBlocks.length; i++) {
					output.append('\n');
					printIndent(indent, output).append("catch ("); //$NON-NLS-1$
					this.catchArguments[i].print(0, output).append(")\n"); //$NON-NLS-1$
					this.catchBlocks[i].printStatement(indent + 1, output);
			}
		//finally
		if (this.finallyBlock != null) {
			output.append('\n');
			printIndent(indent, output).append("finally\n"); //$NON-NLS-1$
			this.finallyBlock.printStatement(indent + 1, output);
		}
		return output;
}
}