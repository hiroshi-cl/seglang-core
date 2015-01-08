package com.github.hiroshi_cl.seglang;

import com.github.hiroshi_cl.seglang.compressors2.Compressor;
import com.github.hiroshi_cl.seglang.compressors2.Compressors;
import com.github.hiroshi_cl.util.Strings;

public class Identifier {
	private final Compressor[] comps;
	private final String[] names;

	public Identifier(final String[] names, final String[] trainings, final Compressors method) {
		this(names, method.getCompressors(trainings));
	}

	public Identifier(final String[] names, final Compressor[] comps) {
		this.names = names;
		this.comps = comps;
	}

	public String identifyName(final String input) {
		double min = Double.POSITIVE_INFINITY;
		int ret = -1;
		for (int i = 0; i < comps.length; i++) {
			double d = comps[i].length(input);
			if (d < min) {
				min = d;
				ret = i;
			}
		}
		return names[ret];
	}

	public int identify(final String input) {
		double min = Double.POSITIVE_INFINITY;
		int ret = -1;
		for (int i = 0; i < comps.length; i++) {
			double d = comps[i].length(input);
			if (d < min) {
				min = d;
				ret = i;
			}
		}
		return ret;
	}

	public int[] identifyLen(final String input, final int maxLen) {
		final int[] ret = new int[maxLen];
		final Compressor.LengthMatrix[] ls = new Compressor.LengthMatrix[comps.length];
		for (int j = 0; j < comps.length; j++)
			ls[j] = comps[j].getLengthMatrix(input);
		for (int j = 0; j < maxLen; j++) {
			double min = Double.POSITIVE_INFINITY;
			int res = -1;
			for (int k = 0; k < comps.length; k++) {
				double d = ls[k].get(0, j + 1);
				if (d < min) {
					min = d;
					res = k;
				}
			}
			ret[j] = res;
		}
		return ret;
	}

	public double identifyRate(final String input, final int ans, final int len) {
		double min = Double.POSITIVE_INFINITY;
		double p = Double.NaN;
		for (int i = 0; i < comps.length; i++) {
			final int[] is = Strings.toCodePoints(input, len);
			double d = comps[i].length(new String(is, 0, is.length));
			if (d < min)
				min = d;
			if (i == ans)
				p = d;
		}
		return 100. * p / min;
	}

	public double identifyRate(final String input, final int len) {
		double min = Double.POSITIVE_INFINITY;
		double p = Double.POSITIVE_INFINITY;
		for (int i = 0; i < comps.length; i++) {
			final int[] is = Strings.toCodePoints(input, len);
			double d = comps[i].length(new String(is, 0, is.length));
			if (d < min) {
				p = min;
				min = d;
			} else if (d < p)
				p = d;
		}
		return 100. * p / min;
	}

	public double identifyDiff(final String input, final int ans, final int len) {
		double min = Double.POSITIVE_INFINITY;
		double p = Double.NaN;
		for (int i = 0; i < comps.length; i++) {
			final int[] is = Strings.toCodePoints(input, len);
			double d = comps[i].length(new String(is, 0, is.length));
			if (d < min)
				min = d;
			if (i == ans)
				p = d;
		}
		return p - min;
	}

	public double identifyDiff(final String input, final int len) {
		double min = Double.POSITIVE_INFINITY;
		double p = Double.POSITIVE_INFINITY;
		for (int i = 0; i < comps.length; i++) {
			final int[] is = Strings.toCodePoints(input, len);
			double d = comps[i].length(new String(is, 0, is.length));
			if (d < min) {
				p = min;
				min = d;
			} else if (d < p)
				p = d;
		}
		return p - min;
	}
}
