package com.github.hiroshi_cl.util;

import java.util.Arrays;

public class TinyHashMap<V> {
	private final int[] keys, masks;
	private final V[] values;
	private final int capacity, cmask;
	private int number = 0;
	private final double rate;
	private static final int DefaultCapacity = 4;
	private static final double DefaultRate = .75;

	public TinyHashMap() {
		this(DefaultCapacity, DefaultRate);
	}

	@SuppressWarnings("unchecked")
	public TinyHashMap(final int capacity, final double rate) {
		if (capacity < 1 || rate >= 1. || Integer.bitCount(capacity) != 1)
			throw null;

		this.capacity = capacity;
		this.cmask = capacity - 1;
		this.keys = new int[capacity];
		this.values = (V[]) new Object[capacity];
		this.masks = new int[capacity];
		this.rate = rate;
		Arrays.fill(keys, -1);
	}

	public final V get(final int key) {
		final int hash = key & cmask;
		for (int j = hash, k = masks[hash]; k > 0; j++, k >>= 1)
			if ((k & 1) > 0) {
				final int jj = j & cmask;
				if (keys[jj] == key)
					return values[jj];
			}
		return null;
	}

	public final int[] keys() {
		final int[] ret = new int[number];
		for (int i = 0, j = 0; i < capacity; i++)
			if (values[i] != null)
				ret[j++] = keys[i];
		return ret;
	}

	private final boolean putInternal(final int key, final V value) {
		// if (key < 0)
		// throw new UnsupportedOperationException();

		if (number >= rate * capacity)
			// throw new BufferOverflowException(capacity);
			return false;

		final int hash = key & cmask;
		for (int j = hash, k = masks[hash]; k > 0; j++, k >>= 1)
			if ((k & 1) > 0) {
				final int jj = j & cmask;
				if (keys[jj] == key) {
					values[jj] = value;
					return true;
				}
			}
		for (int i = 0, k = number + 1; k > 0; i++, k >>= 1) {
			final int j = (hash + i) & cmask;
			if (values[j] == null) {
				keys[j] = key;
				values[j] = value;
				masks[hash] |= 1 << i;
				number++;
				return true;
			}
		}
		return false;
		// throw new BufferOverflowException(capacity);
	}

	private final TinyHashMap<V> expand(final int key, final V value) {
		int newCapacity = capacity;
		loop: while (true) {
			final TinyHashMap<V> h = new TinyHashMap<>(newCapacity <<= 1, rate);
			for (int i = 0; i < capacity; i++)
				for (int j = i, k = masks[i]; k > 0; j++, k >>= 1)
					if ((k & 1) > 0) {
						final int jj = j & cmask;
						if (!h.putInternal(keys[jj], values[jj]))
							continue loop;
					}
			if (h.putInternal(key, value))
				return h;
		}
	}

	public final TinyHashMap<V> put(final int key, final V value) {
		if (putInternal(key, value))
			return this;
		else
			return expand(key, value);
	}
}
