package com.github.obase.kit;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

public class CollectKit {

	private CollectKit() {
	}

	public static boolean isEmpty(Collection<?> colls) {
		return colls == null || colls.size() == 0;
	}

	public static boolean isNotEmpty(Collection<?> colls) {
		return colls != null && colls.size() != 0;
	}

	public static <E> Iterable<E> iterate(final Enumeration<E> enumeration) {

		if (enumeration == null) {
			return null;
		}

		return new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				return new Iterator<E>() {
					@Override
					public boolean hasNext() {
						return enumeration.hasMoreElements();
					}

					@Override
					public E next() {
						return enumeration.nextElement();
					}
				};
			}
		};
	}

}
