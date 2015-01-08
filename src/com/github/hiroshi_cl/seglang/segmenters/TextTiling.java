package com.github.hiroshi_cl.seglang.segmenters;

import com.github.hiroshi_cl.seglang.compressors2.Compressors;
import com.github.hiroshi_cl.seglang.Identifier;
import com.github.hiroshi_cl.seglang.Segmenter;

import java.util.ArrayList;
import java.util.List;

public class TextTiling extends Segmenter {

	private final Identifier identifier;
	private final Compressors method;
	private final double threshold;
	private final int length;
	private final boolean charNorm;

	public TextTiling(final String[] names, final Identifier identifier, final Compressors method,
			final double threshold, final int length, final boolean charNorm) {
		super(names);
		this.identifier = identifier;
		this.method = method;
		this.threshold = threshold;
		this.length = length;
		this.charNorm = charNorm;
	}

	@Override
	public int[] segmentRaw(int[] input, boolean[] isBoundary) {
		final int n = input.length;

		final double[] score = new double[n + 1];
		final int[] left = new int[n + 1];
		final int[] right = new int[n + 1];
		{
			int k = 0;
			for (int i = length; i <= n - length; i++)
				if (isBoundary[i]) {
					score[i] = method.getCompressor(new String(input, i - length, length)).length(
							new String(input, i, length));
					left[i] = k;
					right[k] = i;
					k = i;
				}
			right[k] = n;
			score[0] = score[n] = Double.NEGATIVE_INFINITY;
		}

		final int[] lang = new int[n + 1];
		final int[] prev = new int[n + 1];
		{
			int k = 0;
			for (int i = length; i <= n - length; i++) {
				if (isBoundary[i]) {
					final double depth = charNorm ? (score[i] - score[left[i]]) / (i - left[i])
							+ (score[right[i]] - score[i]) / (right[i] - i) : 2 * score[i] - score[left[i]]
							- score[right[i]];
					if (depth >= threshold) {
						prev[i] = k;
						lang[i] = identifier.identify(new String(input, k, i - k));
						k = i;
					}
				}
			}
			prev[n] = k;
			lang[n] = identifier.identify(new String(input, k, n - k));
		}

		final List<Integer> lng = new ArrayList<Integer>();
		final List<Integer> pos = new ArrayList<Integer>();
		for (int i = n, k = -1; i > 0;) {
			if (k != lang[i]) {
				lng.add(k = lang[i]);
				pos.add(i = prev[i]);
			} else {
				pos.set(pos.size() - 1, i = prev[i]);
			}
		}
		return zipResult(lng, pos, true);
	}
}
