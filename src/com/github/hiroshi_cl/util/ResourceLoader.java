package com.github.hiroshi_cl.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public abstract class ResourceLoader {
	protected Class<? extends ResourceLoader> clazz;
	protected Filter f = null;
	protected final Charset charset;
	protected final String listfile;

	public ResourceLoader(String listfile, Charset charset) {
		this.listfile = listfile;
		this.clazz = getClass();
		this.charset = charset;
	}

	protected void setClazz(Class<? extends ResourceLoader> clazz) {
		this.clazz = clazz;
	}

	public String[] getList() {
		final List<String> list = new ArrayList<>();
		for (final String line : new LineQueue(clazz.getResourceAsStream(listfile), charset))
			if (f == null || f.cond(line))
				list.add(line);
		return list.toArray(new String[0]);
	}

	public abstract String[] getNameList();

	public String[] getResources() {
		final String[] list = getList();
		final int n = list.length;
		final String[] ret = new String[n];
		for (int i = 0; i < n; i++) {
			final StringWriter sw = new StringWriter();
			try (final PrintWriter pw = new PrintWriter(sw)) {
				for (final String line : new LineQueue(clazz.getResourceAsStream(list[i]), charset))
					pw.println(line);
				pw.flush();
			}
			ret[i] = sw.toString();
		}
		return ret;
	}

	public ResourceLoader setFilter(final Filter f) {
		this.f = f;
		return this;
	}

	public abstract static class Filter {
		public abstract boolean cond(final String filename);
	}
}
