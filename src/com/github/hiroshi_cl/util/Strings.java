package com.github.hiroshi_cl.util;

public class Strings {
	public static int[] toCodePoints(final String s) {
		final int m = s.length();
		final int n = s.codePointCount(0, m);
		final char[] cs = s.toCharArray();
		final int[] ret = new int[n];
		for (int i = 0, j = 0; i < n; i++)
			ret[i] = Character.isHighSurrogate(cs[j]) ? Character.toCodePoint(cs[j++], cs[j++]) : cs[j++];
		return ret;
	}

	public static int[] toCodePoints(final String s, final int n) {
		final int m = s.length();
		final char[] cs = s.toCharArray();
		final int[] ret = new int[n];
		for (int i = 0, j = 0; i < n && j < m; i++)
			ret[i] = Character.isHighSurrogate(cs[j]) ? Character.toCodePoint(cs[j++], cs[j++]) : cs[j++];
		return ret;
	}

	public static int[] toCodePointsWithEOF(final String s) {
		final int m = s.length();
		final int n = s.codePointCount(0, m);
		final char[] cs = s.toCharArray();
		final int[] ret = new int[n + 1];
		for (int i = 0, j = 0; i < n; i++)
			ret[i] = Character.isHighSurrogate(cs[j]) ? Character.toCodePoint(cs[j++], cs[j++]) : cs[j++];
		return ret;
	}

	public static int[] toInts(final String s) {
		final char[] cs = s.toCharArray();
		final int[] ret = new int[cs.length + 1];
		for (int i = 0; i < cs.length; i++)
			ret[i] = cs[i];
		return ret;
	}

	public static int alphabetCount(final String[] ss) {
		TinyHashSet set = new TinyHashSet();
		for (final String s : ss)
			for (final int i : toCodePoints(s))
				set = set.add(i);
		return set.size();
	}

	public static int alphabetCount(final int[] is) {
		TinyHashSet set = new TinyHashSet();
		for (int i = 0; i < is.length; i++)
			set = set.add(is[i]);
		return set.size();
	}

	public static String[] split(final int n, final String s) {
		final int[] is = toCodePoints(s);
		final int l = is.length - 1;
		final String[] ret = new String[n];
		for (int i = 0; i < n; i++) {
			final int from = i * l / n;
			final int to = (i + 1) * l / n;
			ret[i] = new String(is, from, to - from);
		}
		return ret;
	}

	public static String[] fold(final String[] ss, final boolean complement) {
		final int n = ss.length;
		final String[] ret = new String[n];
		for (int i = 0; i < n; i++) {
			final StringBuilder sb = new StringBuilder();
			for (int j = complement ? 1 : 0; j < n; j++)
				sb.append(ss[(i + j) % n]);
			ret[i] = sb.toString();
		}
		return ret;
	}
}
