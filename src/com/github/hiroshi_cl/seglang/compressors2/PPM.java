package com.github.hiroshi_cl.seglang.compressors2;

import com.github.hiroshi_cl.util.Strings;

import java.util.Arrays;

public class PPM extends Compressor {
	final int order;
	final PPMTree tree;

	public PPM(final String training, final PPMMethod m, final int o) {
		this(training, m, o, PPMTree.CodePointAlphabetSize);
	}

	public PPM(final String training, final PPMMethod m, final int o, final int alphabetSize) {
		tree = PPMTree.ukkonen(m, o, Strings.toCodePointsWithEOF(training), alphabetSize);
		order = o;
	}

	public PPM(final String training) {
		this(training, PPMTree.DefaultMethod, 5);
	}

	@Override
	public double length(final String input) {
		return tree.entropy(Strings.toCodePoints(input));
	}

	public double chLength(final int[] context, final int c) {
		final int n = context.length;
		final int[] is2 = Arrays.copyOf(context, n + 1);
		is2[n] = c;
		return tree.entropies(is2)[n];
	}

	public static double[] accumulate(final double[] ds) {
		final int l = ds.length;
		final double[] ret = new double[l + 1];
		for (int i = 0; i < l; i++)
			ret[i + 1] = ret[i] + ds[i];
		return ret;
	}

	@Override
	public Length getLengthMatrix(final String input) {
		return getLengthMatrix(Strings.toCodePoints(input));
	}

	@Override
	public Length getLengthMatrix(final int[] input) {
		return new Length(input);
	}

	public class Length implements LengthMatrix {
		final int maxMatch;
		final double[] acc;
		final double[][] shr;

		public Length(final int[] is) {
			final int n = is.length;
			maxMatch = order + 1;
			acc = accumulate(tree.entropies(is));
			shr = new double[n + 1][];
			for (int i = 0; i <= n; i++) {
				final int[] context = Arrays.copyOfRange(is, i, Math.min(i + maxMatch, is.length));
				shr[i] = accumulate(tree.entropies(context));
			}
		}

		@Override
		public double get(final int from, final int to) {
			final int m = to - from;
			return m <= maxMatch ? shr[from][m] : shr[from][maxMatch] - acc[from + maxMatch] + acc[to];
		}

		@Override
		public int maxMatch() {
			return maxMatch;
		}

		@Override
		public double chr(int idx) {
			return acc[idx + 1] - acc[idx];
		}
	}
}
