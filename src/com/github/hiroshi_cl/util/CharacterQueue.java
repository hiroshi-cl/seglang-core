package com.github.hiroshi_cl.util;

import java.io.*;
import java.nio.charset.Charset;

/**
 * <p>
 * Character tokenizer wrapped with Queue interface.
 * </p>
 * 
 * <p>
 * Queue interface is suitable for LL(1) parsing.
 * </p>
 * 
 * @author hiroshi_yamaguchi
 * @see jp.ac.u_tokyo.i.ci.csg.hiroshi_yamaguchi.util.tokenizer_queue
 */
public class CharacterQueue extends AbstractTokenizerQueue<Character> {
	private final BufferedReader reader;

	/**
	 * Build a new instance from a Reader instance.
	 * 
	 * @param r
	 *            Wrapped Reader instance.
	 */
	public CharacterQueue(final Reader r) {
		reader = new BufferedReader(r);
	}

	/**
	 * Build a new instance from an InputStream instance. Used char encoding is
	 * locale-default.
	 * 
	 * @param is
	 *            Wrapped InputStream instance.
	 */
	public CharacterQueue(final InputStream is) {
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
	public CharacterQueue(final InputStream is, Charset cs) {
		this(new InputStreamReader(is, cs));
	}

	/**
	 * Build a new instance from a String instance.
	 * 
	 * @param s
	 *            Wrapped string instance.
	 */
	public CharacterQueue(final String s) {
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
	public CharacterQueue(final File f) throws IOException {
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
	public CharacterQueue(final File f, Charset cs) throws IOException {
		this(new FileInputStream(f), cs);
	}

	@Override
	protected Character getNext() throws IOException {
		final int next = reader.read();
		if (next < 0)
			return null;
		else
			return (char) next;
	}
	
	@Override
	protected void close() throws IOException {
		reader.close();
	}
}
