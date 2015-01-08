package com.github.hiroshi_cl.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * <p>
 * Character tokenizer wrapped with Queue interface.
 * </p>
 * 
 * <p>
 * Queue interface is suitable for LL(1) parsing.
 * </p>
 * 
 * @author hiroshi_yamaguchi
 * @param <T>
 *            String or Character
 * @see jp.ac.u_tokyo.i.ci.csg.hiroshi_yamaguchi.util.tokenizer_queue
 */
public abstract class AbstractTokenizerQueue<T> implements Queue<T> {
	private boolean closed = false;
	private T next = null;

	/**
	 * Extract next element from the Reader. (Internal use.)
	 * 
	 * @return Next element.
	 * @throws java.io.IOException
	 *             Some errors occur.
	 */
	protected abstract T getNext() throws IOException;

	/**
	 * Close the stream.
	 * 
	 * @throws java.io.IOException
	 *             Some errors occur.
	 */
	protected abstract void close() throws IOException;

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final boolean isEmpty() {
		if (closed)
			return true;
		if (next != null)
			return false;
		try {
			next = getNext();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		if (next == null) {
			try {
				close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return closed = true;
		}
		return false;
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override
			public void remove() {
				next();
			}

			@Override
			public T next() {
				return poll();
			}

			@Override
			public boolean hasNext() {
				return !isEmpty();
			}
		};
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final <S> S[] toArray(S[] a) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final synchronized void clear() {
		if (!isEmpty()) {
			closed = true;
			try {
				close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public final boolean offer(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public final synchronized T remove() {
		if (isEmpty())
			throw new NoSuchElementException();
		else {
			final T ret = next;
			next = null;
			return ret;
		}
	}

	@Override
	public final T poll() {
		if (isEmpty())
			return null;
		else {
			final T ret = next;
			next = null;
			return ret;
		}
	}

	@Override
	public T element() {
		if (isEmpty())
			throw new NoSuchElementException();
		else
			return next;
	}

	@Override
	public T peek() {
		if (isEmpty())
			return null;
		else
			return next;
	}

	@Override
	protected void finalize() throws Throwable {
		if (!isEmpty())
			clear();
		super.finalize();
	}
}
