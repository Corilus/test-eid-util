/*
 * (C) 2014 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.util;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Reverses a {@link List}.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class Reversed<T> implements Iterable<T> {

	private final List<T> list;

	/**
	 * Creates a new {@link Reversed}.
	 * 
	 * @param list
	 *            the wrapped {@link List}
	 */
	public Reversed(List<T> list) {
		this.list = list;
	}

	@Override
	public Iterator<T> iterator() {
		final ListIterator<T> i = list.listIterator(list.size());
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return i.hasPrevious();
			}

			@Override
			public T next() {
				return i.previous();
			}

			@Override
			public void remove() {
				i.remove();
			}
		};
	}

}
