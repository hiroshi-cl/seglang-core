package com.github.hiroshi_cl.util;

import java.util.concurrent.Callable;

public class BenchUtil {
	private static final Runtime runtime = Runtime.getRuntime();

	public static void prerun(final Callable<?> callable) {
		System.gc();
		System.out.println("PRERUN : " + callable);
		try {
			callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public static void bench(final String name, final Callable<?> callable) {
		System.gc();
		try {
			final long stMemory = runtime.totalMemory() - runtime.freeMemory();
			final long stTime = System.currentTimeMillis();
			final Object o = callable.call();
			final long enTime = System.currentTimeMillis();
			final long enMemory = runtime.totalMemory() - runtime.freeMemory();
			System.gc();
			final long retMemory = runtime.totalMemory() - runtime.freeMemory();
			System.err.println(o.hashCode());
			System.out.printf("%s\t: %fs, %fM, %fM\n", name, (enTime - stTime) * 1e-3, (enMemory - stMemory) * 1e-6,
					(retMemory - stMemory) * 1e-6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}
}
