package com.github.hiroshi_cl.util;

import java.util.Arrays;

public class TinyHashSet {
	private final int[] keys, masks;
	private final int capacity, cmask;
	private int number = 0;
	private final double rate;
	private static final int DefaultCapacity = 4;
	private static final double DefaultRate = .75;

	public TinyHashSet() {
		this(DefaultCapacity, DefaultRate);
	}

	public TinyHashSet(final int capacity, final double rate) {
		if (capacity < 1 || rate >= 1. || Integer.bitCount(capacity) != 1)
			throw null;

		this.capacity = capacity;
		this.cmask = capacity - 1;
		this.keys = new int[capacity];
		this.masks = new int[capacity];
		this.rate = rate;
		Arrays.fill(keys, -1);
	}

	public final int size() {
		return number;
	}

	public final boolean find(final int key) {
		final int hash = key & cmask;
		for (int j = hash, k = masks[hash]; k > 0; j++, k >>= 1)
			if ((k & 1) > 0) {
				final int jj = j & cmask;
				if (keys[jj] == key)
					return true;
			}
		return false;
	}

	private final boolean addInternal(final int key) {

		if (number >= rate * capacity)
			return false;

		final int hash = key & cmask;
		if (find(key))
			return true;
		for (int i = 0, k = number + 1; k > 0; i++, k >>= 1) {
			final int j = (hash + i) & cmask;
			if (keys[j] < 0) {
				keys[j] = key;
				masks[hash] |= 1 << i;
				number++;
				return true;
			}
		}
		return false;
	}

	private final TinyHashSet expand(final int key) {
		int newCapacity = capacity;
		loop: while (true) {
			final TinyHashSet h = new TinyHashSet(newCapacity <<= 1, rate);
			for (int i = 0; i < capacity; i++)
				for (int j = i, k = masks[i]; k > 0; j++, k >>= 1)
					if ((k & 1) > 0) {
						final int jj = j & cmask;
						if (!h.addInternal(keys[jj]))
							continue loop;
					}
			if (h.addInternal(key))
				return h;
		}
	}

	public final TinyHashSet add(final int key) {
		if (addInternal(key))
			return this;
		else
			return expand(key);
	}
}
