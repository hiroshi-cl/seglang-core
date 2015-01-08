package com.github.hiroshi_cl.seglang.compressors2;

import com.github.hiroshi_cl.util.Strings;

import java.util.Arrays;

import static java.lang.Math.*;

public class MMS extends Compressor {
	final SuffixTree st;
	final double log;
	final boolean safeMode;

	public MMS(final String s) {
		this(s, false);
	}

	public MMS(final String s, final boolean sm) {
		final int[] training = Strings.toCodePointsWithEOF(s);
		st = SuffixTree.ukkonen(training);
		log = log(training.length - 1) / log(2.);
		safeMode = sm;
	}

	@Override
	public double length(final String s) {
		final int[] input = Strings.toCodePointsWithEOF(s);
		final int[] ms = st.matchingStatistics(input);
		double sum = 0.;
		for (int i = 0; i < input.length - 1; i++)
			sum += log / (ms[i] + 1.);
		return sum;
	}

	@Override
	public Length getLengthMatrix(final String input) {
		return getLengthMatrix(Strings.toCodePointsWithEOF(input));
	}

	@Override
	public Length getLengthMatrix(final int[] input) {
		return input[input.length - 1] == 0 ? new Length(input) : new Length(Arrays.copyOf(input, input.length + 1));
	}

	public class Length implements LengthMatrix {
		final int maxMatch;
		final double[] acc;
		final double[][] shr;

		public Length(final int[] input) {
			final int n = input.length;
			final int[] ms = st.matchingStatistics(input);
			int max = 1;
			for (int i : ms)
				max = max(max, i);
			maxMatch = safeMode ? min(max, (int) ceil(log)) : max;
			// If the safe does not exit, this program consume memory too much
			// when input text = training text.
			acc = new double[n + 1];
			for (int i = n - 1; i >= 0; i--)
				acc[i] = acc[i + 1] + log / (ms[i] + 1.);
			shr = new double[n + 1][maxMatch + 1];
			for (int i = n; i >= 0; i--)
				for (int j = 1; j <= maxMatch && i - j >= 0; j++)
					shr[i][j] = shr[i][j - 1] + log / (min(ms[i - j], j) + 1.);
		}

		@Override
		public double get(final int from, final int to) {
			final int m = to - from;
			return m <= maxMatch ? shr[to][m] : shr[to][maxMatch] + acc[from] - acc[to - maxMatch];
		}

		@Override
		public int maxMatch() {
			return maxMatch;
		}

		@Override
		public double chr(int idx) {
			return acc[idx] - acc[idx + 1];
		}
	}
}
