package com.github.hiroshi_cl.seglang;

import com.github.hiroshi_cl.seglang.compressors2.Compressor;
import com.github.hiroshi_cl.util.Strings;

public class Segmenter2 {
	public static int seg2(final String input, final Compressor comp1, final Compressor comp2) {
		final Compressor.LengthMatrix l1 = comp1.getLengthMatrix(input);
		final Compressor.LengthMatrix l2 = comp2.getLengthMatrix(input);
		final int n = Strings.toCodePoints(input).length;
		double min = Double.POSITIVE_INFINITY;
		int res = 0;
		for (int i = 1; i < n; i++) {
			double d = l1.get(0, i) + l2.get(i, n);
			if (d < min) {
				min = d;
				res = i;
			}
		}
		return res;
	}
}
