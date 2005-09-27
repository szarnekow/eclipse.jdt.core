/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.core.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.core.index.Index;
import org.eclipse.jdt.internal.core.util.SimpleSet;

public class SubTypeSearchJob extends PatternSearchJob {

SimpleSet<Index> indexes = new SimpleSet<Index>(5);

public SubTypeSearchJob(SearchPattern pattern, SearchParticipant participant, IJavaSearchScope scope, IndexQueryRequestor requestor) {
	super(pattern, participant, scope, requestor);
}
public void finished() {
	for (Object index : this.indexes.values) {
		if (index != null)
			((Index)index).stopQuery();
	}
}
public boolean search(Index index, IProgressMonitor progressMonitor) {
	if (index == null) return COMPLETE;
	if (!indexes.includes(index)) {
		indexes.add(index);
		index.startQuery();
	}
	return super.search(index, progressMonitor);
}
}
