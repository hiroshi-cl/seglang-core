package com.github.hiroshi_cl.util;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Queue;

/**
 * <p>
 * Word tokenizers wrapped with Queue interface.
 * </p>
 * 
 * <p>
 * Queue interface is suitable for LL(1) parsing.
 * </p>
 * 
 * @author hiroshi_yamaguchi
 * @see jp.ac.u_tokyo.i.ci.csg.hiroshi_yamaguchi.util.tokenizer_queue
 */
public class WordQueue extends AbstractTokenizerQueue<String> {
	private final Queue<Character> chars;

	@Override
	protected String getNext() throws IOException {
		final StringWriter sw = new StringWriter();
		try (final PrintWriter pw = new PrintWriter(sw)) {
			while (!chars.isEmpty() && Character.isWhitespace(chars.peek()))
				chars.poll();
			while (!chars.isEmpty() && !Character.isWhitespace(chars.peek()))
				pw.println(chars.poll());
			pw.flush();
		}
		final String s = sw.toString().trim();
		return s.isEmpty() ? null : s;
	}

	@Override
	protected void close() throws IOException {
		chars.clear();
	}

	/**
	 * Build a new instance from a Reader instance.
	 * 
	 * @param r
	 *            Wrapped Reader instance.
	 */
	public WordQueue(final Reader r) {
		chars = new CharacterQueue(r);
	}

	/**
	 * Build a new instance from an InputStream instance. Used char encoding is
	 * locale-default.
	 * 
	 * @param is
	 *            Wrapped InputStream instance.
	 */
	public WordQueue(final InputStream is) {
		this(new InputStreamReader(is));
	}

	/**
	 * Build a new instance from an InputStream instance using specified
	 * character encoding.
	 * 
	 * @param is
	 *            Wrapped InputStream instance.
	 * @param cs
	 *            Desired character encoding.
	 */
	public WordQueue(final InputStream is, Charset cs) {
		this(new InputStreamReader(is, cs));
	}

	/**
	 * Build a new instance from a String instance.
	 * 
	 * @param s
	 *            Wrapped string instance.
	 */
	public WordQueue(final String s) {
		this(new StringReader(s));
	}

	/**
	 * Build a new instance from a file. Used char encoding is locale-default.
	 * 
	 * @param f
	 *            File name.
	 * @throws java.io.IOException
	 *             For example, corresponding file does not exist or is not
	 *             readable, etc.
	 */
	public WordQueue(final File f) throws IOException {
		this(new FileReader(f));
	}

	/**
	 * Build a new instance from a file using specified character encoding.
	 * 
	 * @param f
	 *            File name.
	 * @param cs
	 *            Desired character encoding.
	 * @throws java.io.IOException
	 *             For example, corresponding file does not exist or is not
	 *             readable, etc.
	 */
	public WordQueue(final File f, Charset cs) throws IOException {
		this(new FileInputStream(f), cs);
	}
}
