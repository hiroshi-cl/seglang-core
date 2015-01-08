package com.github.hiroshi_cl.seglang;

import com.github.hiroshi_cl.seglang.compressors2.Boundary;
import com.github.hiroshi_cl.util.LineQueue;
import com.github.hiroshi_cl.util.Strings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public abstract class Segmenter {
	protected final String[] names;

	public Segmenter(String[] names) {
		this.names = names;
	}

	public abstract int[] segmentRaw(final int[] input, final boolean[] isBoundary);

	public String[] segment(final String s, final Boundary method, final boolean pretty) {
		final int[] input = Strings.toCodePoints(s);
		final int[] res = segmentRaw(input, method.getBoundary(input));
		final int k = res.length / 2 + 1;
		final String[] ret = new String[2 * k];
		for (int p = 0, i = 0; i < k; i++) {
			ret[2 * i] = names[res[i]];
			final int from = p;
			final int to = p = i + k < res.length ? res[i + k] : input.length;
			if (pretty) {
				final StringWriter sw = new StringWriter();
				try (final PrintWriter pw = new PrintWriter(sw)) {
					for (final String line : new LineQueue(new String(input, from, to - from).replace("\n", "↵\n"))) {
						pw.append('\t');
						pw.println(line);
					}
				}
				ret[2 * i + 1] = sw.toString();
			} else
				ret[2 * i + 1] = new String(input, from, to - from);
		}
		return ret;
	}

	public String[] segment(final String s, final Boundary method) {
		return segment(s, method, true);
	}

	public TestResult segmentTest(final String s, final int[] answer, final Boundary method) {
		final int[] is = Strings.toCodePoints(s);
		return new TestResult(segmentRaw(is, method.getBoundary(is)), answer, is.length);
	}

	protected static int[] zipResult(final List<Integer> lng, final List<Integer> pos, final boolean rev) {
		final int k = lng.size();
		final int[] ret = new int[k * 2 - 1];
		if (rev) {
			for (int i = 0; i < k; i++)
				ret[i] = lng.get(k - 1 - i);
			for (int i = 0; i < k - 1; i++)
				ret[i + k] = pos.get(k - 2 - i);
		} else {
			for (int i = 0; i < k; i++)
				ret[i] = lng.get(i);
			for (int i = 0; i < k - 1; i++)
				ret[i + k] = pos.get(i);
		}
		return ret;
	}

	public static class TestResult {
		public final int kResult, kAnswer, detectedLanguage, detectedBoundary, editDistance, length;

		public TestResult(final int[] result, final int[] answer, final int len) {
			final int kr = result.length / 2 + 1;
			final int ka = answer.length / 2 + 1;
			length = len;
			kResult = kr;
			kAnswer = ka;
			detectedLanguage = lcs(Arrays.copyOf(result, kr), Arrays.copyOf(answer, ka));
			detectedBoundary = lcs(Arrays.copyOfRange(result, kr, result.length),
					Arrays.copyOfRange(answer, ka, answer.length));
			editDistance = editDistance(draw(result, length), draw(answer, length));
		}

		public double[] toDoubles() {
			return new double[] { kResult, kAnswer, detectedLanguage, detectedBoundary, editDistance, length, 1. };
		}

		private static int[] draw(final int[] is, final int length) {
			final int[] ret = new int[length];
			final int k = is.length / 2 + 1;
			int p = 0;
			for (int i = 0; i < k - 1; i++)
				while (p < is[i + k])
					ret[p++] = is[i];
			while (p < length)
				ret[p++] = is[k - 1];
			return ret;
		}

		private static int editDistance(final int[] is, final int[] js) {
			if (is.length != js.length)
				throw new RuntimeException(is.length + " " + js.length);
			int cnt = 0;
			for (int i = 0; i < is.length; i++)
				if (is[i] != js[i])
					cnt++;
			return cnt;
		}

		// N : all
		// R : true positive
		// C : true
		public static double[] fpr(final double N, final double R, final double C) {
			return new double[] { 2. * R / (N + C), 1. * R / N, 1. * R / C };
		}

		private static int lcs(final int[] is, final int[] js) {
			final int n = is.length;
			final int m = js.length;
			final int[][] dp = new int[n + 1][m + 1];
			for (int i = 1; i <= n; i++)
				for (int j = 1; j <= m; j++)
					dp[i][j] = Math.max(Math.max(dp[i - 1][j], dp[i][j - 1]),
							is[i - 1] == js[j - 1] ? dp[i - 1][j - 1] + 1 : 0);
			return dp[n][m];
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			for (final double d : toDoubles())
				sb.append(String.format("%d\t", (int) d));
			return sb.toString().trim();
		}
	}
}
