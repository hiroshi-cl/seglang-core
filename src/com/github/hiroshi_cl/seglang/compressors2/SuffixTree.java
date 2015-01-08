package com.github.hiroshi_cl.seglang.compressors2;

import com.github.hiroshi_cl.util.TinyHashMap;

public class SuffixTree {
	private final int[] pattern;
	private final Broom.Normal root;

	private SuffixTree(int[] input, Broom.Normal root) {
		this.pattern = input.clone();
		this.root = root;
	}

	public int[] matchingStatistics(final int[] input) {
		final int l = input.length;
		final int[] ret = new int[l];
		Broom a = root;
		Broom.Normal bn = null;
		for (int i = 0, j = 0, k = 0; i < input.length; i++) {
			while (input[k] != '\0') {
				if (j == 0 ? (bn = a.find(input[k])) == null : input[k] != pattern[bn.s + j])
					break;
				if (++j == bn.l) {
					j = 0;
					a = bn;
				}
				k++;
			}
			ret[i] = k - i;
			a = ((Broom.Normal) a).slink;
			for (bn = a.find(input[k - j]); j > 0 && bn.l <= j; bn = a.find(input[k - j])) {
				j -= bn.l;
				a = bn;
			}
		}
		return ret;
	}

	public static SuffixTree ukkonen(final int[] cs) {
		final Broom.Normal root = Broom.Normal.makeRoot();
		Broom a = root;
		Broom.Normal bn = null;
		for (int s = 0, l = 0; s + l < cs.length;) {
			if (l == 0) {
				while ((bn = a.find(cs[s])) == null) {
					final Broom.Normal an = (Broom.Normal) a;
					an.put(cs[s], new Broom.Normal(s, cs.length));
					a = an.slink;
				}
			} else if (cs[s + l] != cs[bn.s + l]) {
				Broom.Normal pre = null;
				while (l > 0) {
					// split
					final Broom.Normal an = (Broom.Normal) a;
					final Broom.Normal newArc = new Broom.Normal(bn.s, bn.s + l);
					bn.l -= l;
					an.put(cs[s], newArc);
					newArc.put(cs[bn.s += l], bn);
					newArc.put(cs[s + l], new Broom.Normal(s + l, cs.length));
					// update slink
					if (pre != null)
						pre.slink = newArc;
					pre = newArc;
					// go through slink and canonize
					a = an.slink;
					for (bn = a.find(cs[s]); l > 0 && bn.l <= l; bn = bn.find(cs[s])) {
						s += bn.l;
						l -= bn.l;
						a = bn;
					}
				}
				if (pre != null) {
					pre.slink = a;
					pre = null;
				}
				continue;
			}
			// go to next character
			if (++l == bn.l) {
				a = bn;
				s += bn.l;
				l = 0;
				bn = null;
			}
		}
		return new SuffixTree(cs, root);
	}

	private static abstract class Broom {

		public abstract Normal find(final int c);

		public static class Top extends Broom {
			public final Normal root = new Normal(-1, 0);

			public Top() {
				root.slink = this;
			}

			@Override
			public final Normal find(int c) {
				return root;
			}
		}

		public static class Normal extends Broom {
			public Broom slink = null;
			public int s, l; // [s, s + l)
			public TinyHashMap<Normal> children = null;

			public Normal(int s, int t) {
				this.s = s;
				this.l = t - s;
			}

			@Override
			public final Normal find(int c) {
				return children == null ? null : children.get(c);
			}

			public final void put(int c, Normal a) {
				if (children == null)
					children = new TinyHashMap<>(4, .75);
				children = children.put(c, a);
			}

		}

		public static Normal makeRoot() {
			return new Top().root;
		}
	}
}
