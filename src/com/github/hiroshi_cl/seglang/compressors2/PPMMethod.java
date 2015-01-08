package com.github.hiroshi_cl.seglang.compressors2;

import static java.lang.Math.log;

public enum PPMMethod {
	A {
		@Override
		double esc(final int n, final int u) {
			return bit(n);
		}

		@Override
		double chr(final int n, final int u, final int c) {
			return u == 0 ? bit(n) - bit(c) : bit(n + 1) - bit(c);
		}
	},
	C {
		@Override
		double esc(final int n, final int u) {
			return bit(n + u) - bit(u);
		}

		@Override
		double chr(final int n, final int u, final int c) {
			return bit(n + u) - bit(c);
		}
	},
	D {
		@Override
		double esc(final int n, final int u) {
			return 1. + bit(n) - bit(u);
		}

		@Override
		double chr(final int n, final int u, final int c) {
			return 2. + 2. * bit(n) - bit(2 * n - u) - bit(c);
		}
	};
	abstract double esc(int n, int u);

	abstract double chr(int n, int u, int c);

	private static double bit(int n) {
		return n == 0 ? 0. : log(n) / log(2.);
	}
}