package com.github.hiroshi_cl.seglang.segmenters;

import com.github.hiroshi_cl.seglang.compressors2.Compressor;
import com.github.hiroshi_cl.seglang.Segmenter;

import com.github.hiroshi_cl.seglang.compressors2.MMS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class MMSSegWAC extends Segmenter {

	private final double gamma;
	private final MMS[] comps;

	public MMSSegWAC(final String[] names, MMS[] comps, double gamma) {
		super(names);
		this.comps = comps;
		this.gamma = gamma;
	}

	@Override
	public int[] segmentRaw(int[] input, boolean[] isBoundary) {
		final int n = input.length;
		final int m = comps.length;
		final double P = log(n) + log(m) + gamma;
		final Compressor.LengthMatrix[] ls = new Compressor.LengthMatrix[m];
		for (int i = 0; i < m; i++)
			ls[i] = comps[i].getLengthMatrix(input);

		final double[] dp = new double[n + 1];
		for (int i = 0; i < n; i++)
			dp[i] = Double.POSITIVE_INFINITY;
		final int[] lang = new int[n + 1];
		final int[] prev = new int[n + 1];
		final double[] e = new double[m];
		final int[] ep = new int[m];
		Arrays.fill(e, Double.POSITIVE_INFINITY);
		for (int i = n - 1; i >= 0; i--) {
			for (int k = 0; k < m; k++) {
				e[k] += ls[k].chr(i);
				{
					final int j = i + ls[k].maxMatch();
					if (j <= n && isBoundary[j]) {
						final double d = dp[j] + ls[k].get(i, j) + P;
						if (d < e[k]) {
							e[k] = d;
							ep[k] = j;
						}
					}
				}
				double min = e[k];
				int p = ep[k];
				for (int j = min(i + ls[k].maxMatch() + 1, n - 1); j > i; j--)
					if (isBoundary[j]) {
						final double d = dp[j] + ls[k].get(i, j) + P;
						if (d < min) {
							min = d;
							p = j;
						}
					}
				if (min < dp[i]) {
					dp[i] = min;
					lang[i] = k;
					prev[i] = p;
				}
			}
		}
		final List<Integer> lng = new ArrayList<Integer>();
		final List<Integer> pos = new ArrayList<Integer>();
		for (int i = 0, k = -1; i < n;) {
			if (k != lang[i]) {
				lng.add(k = lang[i]);
				pos.add(i = prev[i]);
			} else {
				pos.set(pos.size() - 1, i = prev[i]);
			}
		}
		return zipResult(lng, pos, false);
	}
}
