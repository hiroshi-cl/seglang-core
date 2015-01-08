package com.github.hiroshi_cl.seglang.compressors2;

import com.github.hiroshi_cl.util.TinyHashMap;

public class PPMTree {
	private final PPMMethod method;
	private final int order;
	private final int[] pattern;
	private final Broom.Normal root;
	public static final int DefaultAlphabetSize = 1 << Character.SIZE;
	public static final int CodePointAlphabetSize = Character.MAX_CODE_POINT + 1;
	public static final PPMMethod DefaultMethod = PPMMethod.C;

	private PPMTree(final PPMMethod method, final int order, final int[] input, final Broom.Normal root) {
		this.method = method;
		this.pattern = input.clone();
		this.root = root;
		this.order = order;
		root.pre_count();
		root.pre_exclusion();
		root.pre_branchCut(order);
	}

	public double entropy(final int[] input) {
		double entropy = 0.;
		Broom a = root;
		Broom.Normal bn = null;
		for (int i = 0, j = 0, k = 0; i < input.length; i++, k++) {
			int n = j == 0 ? a.n() : bn.n + 1;
			int u = j == 0 ? a.u() : 1;
			while (j == 0 ? (bn = a.find(input[i])) == null : input[i] != pattern[bn.s + j]) {
				entropy += method.esc(n, u);
				final Broom pa = a;
				final Broom.Normal pbn = bn;
				final int pj = j;
				a = ((Broom.Normal) a).slink;
				k--;
				for (bn = a.find(input[i - j]); j > 0 && bn.l <= j; bn = a.find(input[i - j])) {
					j -= bn.l;
					a = bn;
				}

				n = j == 0 ? a.n() : bn.n + 1;
				u = j == 0 ? a.u() : 1;
				n -= pj == 0 ? pa.ne() : j == 0 ? a.c(pattern[pbn.s + pj]) + 1 : bn.n + 1;
				u -= pj == 0 ? pa.ue() : 1;
			}
			final int c = j == 0 ? a.c(input[i]) : bn.n;
			entropy += method.chr(n, u, c);

			if (k == order) {
				a = ((Broom.Normal) a).slink;
				k--;
				for (bn = a.find(input[i - j]); j > 0 && bn.l <= j; bn = a.find(input[i - j])) {
					j -= bn.l;
					a = bn;
				}
			}
			if (++j == bn.l) {
				j = 0;
				a = bn;
			}
		}
		return entropy;
	}

	public double[] entropies(final int[] input) {
		final int l = input.length;
		final double[] entropies = new double[l];
		Broom a = root;
		Broom.Normal bn = null;
		for (int i = 0, j = 0, k = 0; i < input.length; i++, k++) {
			int n = j == 0 ? a.n() : bn.n + 1;
			int u = j == 0 ? a.u() : 1;
			while (j == 0 ? (bn = a.find(input[i])) == null : input[i] != pattern[bn.s + j]) {
				entropies[i] += method.esc(n, u);
				final Broom pa = a;
				final Broom.Normal pbn = bn;
				final int pj = j;
				a = ((Broom.Normal) a).slink;
				k--;
				for (bn = a.find(input[i - j]); j > 0 && bn.l <= j; bn = a.find(input[i - j])) {
					j -= bn.l;
					a = bn;
				}

				n = j == 0 ? a.n() : bn.n + 1;
				u = j == 0 ? a.u() : 1;
				n -= pj == 0 ? pa.ne() : j == 0 ? a.c(pattern[pbn.s + pj]) + 1 : bn.n + 1;
				u -= pj == 0 ? pa.ue() : 1;
			}
			final int c = j == 0 ? a.c(input[i]) : bn.n;
			entropies[i] += method.chr(n, u, c);

			if (k == order) {
				a = ((Broom.Normal) a).slink;
				k--;
				for (bn = a.find(input[i - j]); j > 0 && bn.l <= j; bn = a.find(input[i - j])) {
					j -= bn.l;
					a = bn;
				}
			}
			if (++j == bn.l) {
				j = 0;
				a = bn;
			}
		}
		return entropies;
	}

	public static PPMTree ukkonen(final PPMMethod method, final int order, final int[] cs, final int alphabetSize) {
		final Broom.Normal root = Broom.Normal.makeRoot(alphabetSize);
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
		return new PPMTree(method, order, cs, root);
	}

	private static abstract class Broom {

		public abstract Normal find(final int c);

		public abstract int n();

		public abstract int u();

		public abstract int ne();

		public abstract int ue();

		public abstract int c(int c);

		public static class Top extends Broom {
			public final Normal root = new Normal(-1, 0);
			private final int u;

			public Top(final int alphabetSize) {
				root.slink = this;
				u = alphabetSize;
			}

			@Override
			public final Normal find(int c) {
				return root;
			}

			@Override
			public int n() {
				return u + root.ne;
			}

			@Override
			public int u() {
				return root.u;
			}

			@Override
			public int ne() {
				throw null;
			}

			@Override
			public int ue() {
				throw null;
			}

			@Override
			public int c(int c) {
				return 1;
			}

		}

		public static class Normal extends Broom {
			public Broom slink = null;
			public int s, l; // [s, s + l)
			public TinyHashMap<Normal> children = null;

			private int n; // all character count
			private int u; // different character kind
			private int ne; // all character count about suffix link node
							// considering exclusion
			private int ue; // different character kind about suffix link node
							// considering exclusion

			private void pre_count() {
				if (children == null) {
					n = 1;
					u = 1;
				} else {
					final int[] keys = children.keys();
					u = keys.length;
					n = 0;
					for (int i = 0; i < keys.length; i++) {
						final Normal a = children.get(keys[i]);
						a.pre_count();
						n += a.n;
					}
				}
			}

			private void pre_exclusion() {
				if (children != null) {
					final int[] keys = children.keys();
					final Broom b = slink;
					ue = u;
					ne = 0;
					for (int i = 0; i < keys.length; i++)
						ne += b.c(keys[i]);
					for (int i = 0; i < keys.length; i++)
						children.get(keys[i]).pre_exclusion();
				}
			}

			private void pre_branchCut(final int o) {
				if (children != null)
					if (o < 0)
						children = null;
					else {
						final int[] keys = children.keys();
						final int no = o - l;
						for (int i = 0; i < keys.length; i++)
							children.get(keys[i]).pre_branchCut(no);
					}
			}

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

			@Override
			public int n() {
				return n + u;
			}

			@Override
			public int u() {
				return u;
			}

			@Override
			public int ne() {
				return ne + ue;
			}

			@Override
			public int ue() {
				return ue;
			}

			@Override
			public int c(int c) {
				if (children == null)
					return 0;
				final Normal a = children.get(c);
				return a == null ? 0 : a.n;
			}
		}

		public static Normal makeRoot(final int alphabetSize) {
			return new Top(alphabetSize).root;
		}
	}
}
