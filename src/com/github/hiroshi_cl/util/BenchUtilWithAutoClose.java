package com.github.hiroshi_cl.util;

public class BenchUtilWithAutoClose implements AutoCloseable {
	private static final Runtime runtime = Runtime.getRuntime();

	private Object result = null;
	private final String name;
	private final long stMemory;
	private final long stTime;

	public BenchUtilWithAutoClose(String name) {
		this.name = name;
		System.gc();
		stMemory = runtime.totalMemory() - runtime.freeMemory();
		stTime = System.currentTimeMillis();
	}

	public void submitResult(final Object o) {
		result = o;
	}

	@Override
	public void close() {
		final long enTime = System.currentTimeMillis();
		final long enMemory = runtime.totalMemory() - runtime.freeMemory();
		System.gc();
		final long retMemory = runtime.totalMemory() - runtime.freeMemory();
		System.err.println(result.hashCode());
		System.out.printf("%s\t: %fs, %fM, %fM\n", name, (enTime - stTime) * 1e-3, (enMemory - stMemory) * 1e-6,
				(retMemory - stMemory) * 1e-6);
		result = null;
		System.gc();
	}
}
