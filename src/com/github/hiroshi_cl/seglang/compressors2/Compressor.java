package com.github.hiroshi_cl.seglang.compressors2;

public abstract class Compressor {

	public abstract double length(final String input);

	public abstract LengthMatrix getLengthMatrix(final String input);

	public LengthMatrix getLengthMatrix(final int[] input) {
		return getLengthMatrix(new String(input, 0, input.length));
	}

	public static interface LengthMatrix {
		public double chr(final int idx);

		public double get(final int from, final int to);

		public int maxMatch();
	}
}
