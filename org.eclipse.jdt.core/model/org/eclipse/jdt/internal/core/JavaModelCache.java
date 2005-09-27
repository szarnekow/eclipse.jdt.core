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
package org.eclipse.jdt.internal.core;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;

/**
 * The cache of java elements to their respective info.
 */
public class JavaModelCache {
	public static final int DEFAULT_PROJECT_SIZE = 5;  // average 25552 bytes per project.
	public static final int DEFAULT_ROOT_SIZE = 50; // average 2590 bytes per root -> maximum size : 25900*BASE_VALUE bytes
	public static final int DEFAULT_PKG_SIZE = 500; // average 1782 bytes per pkg -> maximum size : 178200*BASE_VALUE bytes
	public static final int DEFAULT_OPENABLE_SIZE = 500; // average 6629 bytes per openable (includes children) -> maximum size : 662900*BASE_VALUE bytes
	public static final int DEFAULT_CHILDREN_SIZE = 500*20; // average 20 children per openable
	
	/**
	 * Active Java Model Info
	 */
	protected JavaModelInfo modelInfo;
	
	/**
	 * Cache of open projects.
	 */
	protected HashMap<IJavaProject, JavaProjectElementInfo> projectCache;
	
	/**
	 * Cache of open package fragment roots.
	 */
	protected ElementCache<IPackageFragmentRoot, PackageFragmentRootInfo> rootCache;
	
	/**
	 * Cache of open package fragments
	 */
	protected ElementCache<IPackageFragment, PackageFragmentInfo> pkgCache;

	/**
	 * Cache of open compilation unit and class files
	 */
	protected ElementCache<Openable, OpenableElementInfo> openableCache;

	/**
	 * Cache of open children of openable Java Model Java elements
	 */
	protected Map<IJavaElement, Object> childrenCache;
	
public JavaModelCache() {
	// set the size of the caches in function of the maximum amount of memory available
	double ratio =  Runtime.getRuntime().maxMemory() / 64000000; // 64000000 is the base memory for most JVM
	this.projectCache = new HashMap<IJavaProject, JavaProjectElementInfo>(DEFAULT_PROJECT_SIZE); // NB: Don't use a LRUCache for projects as they are constantly reopened (e.g. during delta processing)
	this.rootCache = new ElementCache<IPackageFragmentRoot, PackageFragmentRootInfo>((int) (DEFAULT_ROOT_SIZE * ratio));
	this.pkgCache = new ElementCache<IPackageFragment, PackageFragmentInfo>((int) (DEFAULT_PKG_SIZE * ratio));
	this.openableCache = new ElementCache<Openable, OpenableElementInfo>((int) (DEFAULT_OPENABLE_SIZE * ratio));
	this.childrenCache = new HashMap<IJavaElement, Object>((int) (DEFAULT_CHILDREN_SIZE * ratio));
}

/**
 *  Returns the info for the element.
 */
public Object getInfo(IJavaElement element) {
	switch (element.getElementType()) {
		case IJavaElement.JAVA_MODEL:
			return this.modelInfo;
		case IJavaElement.JAVA_PROJECT:
			return this.projectCache.get(element);
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			return this.rootCache.get((IPackageFragmentRoot) element);
		case IJavaElement.PACKAGE_FRAGMENT:
			return this.pkgCache.get((IPackageFragment) element);
		case IJavaElement.COMPILATION_UNIT:
		case IJavaElement.CLASS_FILE:
			return this.openableCache.get((Openable) element);
		default:
			return this.childrenCache.get(element);
	}
}

/**
 *  Returns the info for this element without
 *  disturbing the cache ordering.
 */
protected Object peekAtInfo(IJavaElement element) {
	switch (element.getElementType()) {
		case IJavaElement.JAVA_MODEL:
			return this.modelInfo;
		case IJavaElement.JAVA_PROJECT:
			return this.projectCache.get(element);
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			return this.rootCache.peek((IPackageFragmentRoot) element);
		case IJavaElement.PACKAGE_FRAGMENT:
			return this.pkgCache.peek((IPackageFragment) element);
		case IJavaElement.COMPILATION_UNIT:
		case IJavaElement.CLASS_FILE:
			return this.openableCache.peek((Openable) element);
		default:
			return this.childrenCache.get(element);
	}
}

/**
 * Remember the info for the element.
 */
protected void putInfo(IJavaElement element, Object info) {
	switch (element.getElementType()) {
		case IJavaElement.JAVA_MODEL:
			this.modelInfo = (JavaModelInfo) info;
			break;
		case IJavaElement.JAVA_PROJECT:
			this.projectCache.put((IJavaProject) element, (JavaProjectElementInfo) info);
			this.rootCache.ensureSpaceLimit(((JavaElementInfo) info).children.length, element);
			break;
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			this.rootCache.put((IPackageFragmentRoot) element, (PackageFragmentRootInfo) info);
			this.pkgCache.ensureSpaceLimit(((JavaElementInfo) info).children.length, element);
			break;
		case IJavaElement.PACKAGE_FRAGMENT:
			this.pkgCache.put((IPackageFragment) element, (PackageFragmentInfo) info);
			this.openableCache.ensureSpaceLimit(((JavaElementInfo) info).children.length, element);
			break;
		case IJavaElement.COMPILATION_UNIT:
		case IJavaElement.CLASS_FILE:
			this.openableCache.put((Openable) element, (OpenableElementInfo) info);
			break;
		default:
			this.childrenCache.put(element, info);
	}
}
/**
 * Removes the info of the element from the cache.
 */
protected void removeInfo(IJavaElement element) {
	switch (element.getElementType()) {
		case IJavaElement.JAVA_MODEL:
			this.modelInfo = null;
			break;
		case IJavaElement.JAVA_PROJECT:
			this.projectCache.remove(element);
			this.rootCache.resetSpaceLimit(DEFAULT_ROOT_SIZE, element);
			break;
		case IJavaElement.PACKAGE_FRAGMENT_ROOT:
			this.rootCache.remove((IPackageFragmentRoot) element);
			this.pkgCache.resetSpaceLimit(DEFAULT_PKG_SIZE, element);
			break;
		case IJavaElement.PACKAGE_FRAGMENT:
			this.pkgCache.remove((IPackageFragment) element);
			this.openableCache.resetSpaceLimit(DEFAULT_OPENABLE_SIZE, element);
			break;
		case IJavaElement.COMPILATION_UNIT:
		case IJavaElement.CLASS_FILE:
			this.openableCache.remove((Openable) element);
			break;
		default:
			this.childrenCache.remove(element);
	}
}
public String toStringFillingRation(String prefix) {
	StringBuffer buffer = new StringBuffer();
	buffer.append(prefix);
	buffer.append("Project cache: "); //$NON-NLS-1$
	buffer.append(this.projectCache.size());
	buffer.append(" projects\n"); //$NON-NLS-1$
	buffer.append(prefix);
	buffer.append("Root cache["); //$NON-NLS-1$
	buffer.append(this.rootCache.getSpaceLimit());
	buffer.append("]: "); //$NON-NLS-1$
	buffer.append(NumberFormat.getInstance().format(this.rootCache.fillingRatio()));
	buffer.append("%\n"); //$NON-NLS-1$
	buffer.append(prefix);
	buffer.append("Package cache["); //$NON-NLS-1$
	buffer.append(this.pkgCache.getSpaceLimit());
	buffer.append("]: "); //$NON-NLS-1$
	buffer.append(NumberFormat.getInstance().format(this.pkgCache.fillingRatio()));
	buffer.append("%\n"); //$NON-NLS-1$
	buffer.append(prefix);
	buffer.append("Openable cache["); //$NON-NLS-1$
	buffer.append(this.openableCache.getSpaceLimit());
	buffer.append("]: "); //$NON-NLS-1$
	buffer.append(NumberFormat.getInstance().format(this.openableCache.fillingRatio()));
	buffer.append("%\n"); //$NON-NLS-1$
	return buffer.toString();
}
}
