package com.github.hiroshi_cl.seglang.compressors2;

import java.util.WeakHashMap;

public enum Compressors {
	PPM {
		@Override
		public synchronized PPM getCompressor(String s) {
			if (cache.containsKey(s)) {
				final PPM ppm = (PPM) cache.get(s);
				if (ppm != null)
					return ppm;
			}
			final PPM ppm = new PPM(s);
			cache.put(s, ppm);
			return ppm;
		}

		@Override
		public PPM[] getCompressors(final String[] ss) {
			final int n = ss.length;
			final PPM[] ret = new PPM[n];
			for (int i = 0; i < n; i++)
				ret[i] = getCompressor(ss[i]);
			return ret;
		}
	},
	MMS {
		@Override
		public synchronized MMS getCompressor(String s) {
			if (cache.containsKey(s)) {
				final MMS mms = (MMS) cache.get(s);
				if (mms != null)
					return mms;
			}
			final MMS mms = new MMS(s);
			cache.put(s, mms);
			return mms;
		}

		@Override
		public MMS[] getCompressors(final String[] ss) {
			final int n = ss.length;
			final MMS[] ret = new MMS[n];
			for (int i = 0; i < n; i++)
				ret[i] = getCompressor(ss[i]);
			return ret;
		}
	};
	public abstract Compressor getCompressor(final String s);

	public abstract Compressor[] getCompressors(final String[] ss);

	protected final WeakHashMap<String, Compressor> cache = new WeakHashMap<>();
}