package com.github.hiroshi_cl.seglang.segmenters;

import com.github.hiroshi_cl.seglang.compressors2.Compressor;
import com.github.hiroshi_cl.seglang.Segmenter;
import com.github.hiroshi_cl.seglang.compressors2.PPM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.*;

public class Teahan extends Segmenter {

	private final PPM[] comps;

	public Teahan(final String[] names, PPM[] comps) {
		super(names);
		this.comps = comps;
	}

	@Override
	public int[] segmentRaw(int[] input, boolean[] isBoundary) {
		final int n = input.length;
		final int m = comps.length;
		final Compressor.LengthMatrix[] ls = new Compressor.LengthMatrix[m];
		for (int i = 0; i < m; i++)
			ls[i] = comps[i].getLengthMatrix(input);

		final double[][] dp = new double[n + 1][2];
		for (int i = 1; i <= n; i++)
			for (int j = 0; j < 2; j++)
				dp[i][j] = Double.POSITIVE_INFINITY;
		final int[][] lang = new int[n + 1][2];
		final int[][] prev = new int[n + 1][2];
		final double[] e = new double[m];
		final int[] ep = new int[m];
		Arrays.fill(e, Double.POSITIVE_INFINITY);
		for (int i = 1; i <= n; i++) {
			for (int k = 0; k < m; k++) {
				e[k] += ls[k].chr(i - 1);
				{
					final int j = i - ls[k].maxMatch();
					if (j >= 0 && isBoundary[j]) {
						final double d = (lang[j][0] != k ? dp[j][0] : dp[j][1]) + ls[k].get(j, i)
								+ comps[k].chLength(Arrays.copyOfRange(input, j, i), Integer.MAX_VALUE);
						if (d < e[k]) {
							e[k] = d;
							ep[k] = j;
						}
					}
				}
				double min = e[k];
				int p = ep[k];
				for (int j = max(i - ls[k].maxMatch() + 1, 0); j < i; j++)
					if (isBoundary[j]) {
						final double d = (lang[j][0] != k ? dp[j][0] : dp[j][1]) + ls[k].get(j, i)
								+ comps[k].chLength(Arrays.copyOfRange(input, j, i), Integer.MAX_VALUE);
						if (d < min) {
							min = d;
							p = j;
						}
					}
				if (min < dp[i][0]) {
					dp[i][1] = dp[i][0];
					lang[i][1] = lang[i][0];
					prev[i][1] = prev[i][0];
					dp[i][0] = min;
					lang[i][0] = k;
					prev[i][0] = p;
				} else if (min < dp[i][1]) {
					dp[i][1] = min;
					lang[i][1] = k;
					prev[i][1] = p;
				}
			}
		}
		final List<Integer> lng = new ArrayList<Integer>();
		final List<Integer> pos = new ArrayList<Integer>();
		for (int i = n, k = -1; i > 0;) {
			if (k == lang[i][0]) {
				lng.add(k = lang[i][1]);
				pos.add(i = prev[i][1]);
			} else {
				lng.add(k = lang[i][0]);
				pos.add(i = prev[i][0]);
			}
		}
		return zipResult(lng, pos, true);
	}

}
