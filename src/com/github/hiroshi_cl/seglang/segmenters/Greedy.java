package com.github.hiroshi_cl.seglang.segmenters;

import com.github.hiroshi_cl.seglang.compressors2.Compressor;
import com.github.hiroshi_cl.seglang.Segmenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Greedy extends Segmenter {

	private final Compressor[] comps;

	public Greedy(final String[] names, Compressor[] comps) {
		super(names);
		this.comps = comps;
	}

	@Override
	public int[] segmentRaw(int[] input, boolean[] isBoundary) {
		final int n = input.length;
		final int m = comps.length;
		final Compressor.LengthMatrix[] ls = new Compressor.LengthMatrix[m];
		for (int i = 0; i < m; i++) {
			ls[i] = comps[i].getLengthMatrix(input);
		}

		final double[] len = new double[m];
		final int[] lang = new int[n + 1];
		final int[] prev = new int[n + 1];
		for (int i = 1, k = 0; i <= n; i++) {
			for (int j = 0; j < m; j++)
				len[j] += ls[j].chr(i - 1);
			if (isBoundary[i]) {
				int min = 0;
				for (int j = 1; j < m; j++)
					if (len[j] < len[min])
						min = j;
				lang[i] = min;
				prev[i] = k;
				k = i;
				Arrays.fill(len, 0.);
			}
		}

		final List<Integer> lng = new ArrayList<Integer>();
		final List<Integer> pos = new ArrayList<Integer>();
		for (int i = n, k = -1; i > 0;)
			if (k != lang[i]) {
				lng.add(k = lang[i]);
				pos.add(i = prev[i]);
			} else {
				pos.set(pos.size() - 1, i = prev[i]);
			}
		return zipResult(lng, pos, true);
	}
}
