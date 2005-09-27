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
package org.eclipse.jdt.internal.core.util;

/**
 * A simple lookup table is a non-synchronized Hashtable, whose keys
 * and values are Objects. It also uses linear probing to resolve collisions
 * rather than a linked list of hash table entries.
 */
public final class SimpleSet<T> implements Cloneable {

// to avoid using Enumerations, walk the individual values skipping nulls
public Object[] values;
public int elementSize; // number of elements in the table
public int threshold;

public SimpleSet() {
	this(13);
}

public SimpleSet(int size) {
	if (size < 3) size = 3;
	this.elementSize = 0;
	this.threshold = size + 1; // size is the expected number of elements
	this.values = new Object[2 * size + 1];
}

public Object add(T object) {
	int length = values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = values[index]) != null) {
		if (current.equals(object)) return values[index] = object;
		if (++index == length) index = 0;
	}
	values[index] = object;

	// assumes the threshold is never equal to the size of the table
	if (++elementSize > threshold) rehash();
	return object;
}

public void clear() {
	for (int i = this.values.length; --i >= 0;)
		this.values[i] = null;
	this.elementSize = 0;
}
@SuppressWarnings("unchecked") 
public Object clone() throws CloneNotSupportedException {
	SimpleSet<T> result = (SimpleSet<T>) super.clone();
	result.elementSize = this.elementSize;
	result.threshold = this.threshold;

	int length = this.values.length;
	result.values = (T[])new Object[length];
	System.arraycopy(this.values, 0, result.values, 0, length);
	return result;
}

public boolean includes(T object) {
	int length = values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = values[index]) != null) {
		if (current.equals(object)) return true;
		if (++index == length) index = 0;
	}
	return false;
}

public Object remove(T object) {
	int length = values.length;
	int index = (object.hashCode() & 0x7FFFFFFF) % length;
	Object current;
	while ((current = values[index]) != null) {
		if (current.equals(object)) {
			elementSize--;
			Object oldValue = values[index];
			values[index] = null;
			if (values[index + 1 == length ? 0 : index + 1] != null)
				rehash(); // only needed if a possible collision existed
			return oldValue;
		}
		if (++index == length) index = 0;
	}
	return null;
}

private void rehash() {
	SimpleSet<Object>newSet = new SimpleSet<Object>(elementSize * 2); // double the number of expected elements
	Object current;
	for (int i = values.length; --i >= 0;)
		if ((current = values[i]) != null)
			newSet.add(current);

	this.values = newSet.values;
	this.elementSize = newSet.elementSize;
	this.threshold = newSet.threshold;
}

public String toString() {
	StringBuffer s = new StringBuffer();
	for (Object value: this.values) {
		if (value != null)
			s.append(value).append('\n');
	}
	return s.toString();
}

/**
 * Return values as an array of type parameter class.
 * Indexes of non-null values of returned array are continuous.
 *
 * @return Warning: null if set is empty!
 */
@SuppressWarnings("unchecked") 
public T[] values() {
	if (this.elementSize == 0) return null;
	T[] array = null;
	int count = 0;
	for (Object value : this.values) {
		if (value != null) {
			if (array == null) {
				array = (T[]) java.lang.reflect.Array.newInstance(value.getClass(), elementSize);
			}
			array[count++] = (T) value;
		}
	}
	return array;
}
}
