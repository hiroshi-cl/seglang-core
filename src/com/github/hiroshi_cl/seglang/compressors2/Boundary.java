package com.github.hiroshi_cl.seglang.compressors2;

import com.github.hiroshi_cl.util.UnicodeBlockName;

import java.util.Arrays;

public enum Boundary {
	All {
		@Override
		public boolean[] getBoundary(int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			Arrays.fill(isBoundary, true);
			return isBoundary;
		}
	},
	Words_EXT {
		@Override
		public boolean[] getBoundary(final int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			for (int i = 1; i < is.length; i++)
				isBoundary[i] = Character.isWhitespace(is[i - 1]) || UnicodeBlockName.notSeparated(is[i - 1]);
			isBoundary[0] = isBoundary[is.length] = true;
			return isBoundary;
		}
	},
	Words {
		@Override
		public boolean[] getBoundary(final int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			for (int i = 1; i < is.length; i++)
				isBoundary[i] = Character.isWhitespace(is[i - 1]);
			isBoundary[0] = isBoundary[is.length] = true;
			return isBoundary;
		}
	},
	Periods {
		@Override
		public boolean[] getBoundary(int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			Arrays.fill(isBoundary, true);
			for (int i = 1; i < is.length; i++)
				isBoundary[i] = is[i - 1] == '.';
			isBoundary[0] = isBoundary[is.length] = true;
			return isBoundary;
		}
	},
	Lines {
		@Override
		public boolean[] getBoundary(int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			Arrays.fill(isBoundary, true);
			for (int i = 1; i < is.length; i++)
				isBoundary[i] = is[i - 1] == '\r' || is[i - 1] == '\n';
			isBoundary[0] = isBoundary[is.length] = true;
			return isBoundary;
		}
	},
	Paragraphs {
		@Override
		public boolean[] getBoundary(int[] is) {
			final boolean[] isBoundary = new boolean[is.length + 1];
			for (int i = 2; i < is.length; i++)
				isBoundary[i] = Character.getType(is[i - 2]) == Character.LINE_SEPARATOR
						&& Character.getType(is[i - 1]) == Character.LINE_SEPARATOR;
			isBoundary[0] = isBoundary[is.length] = true;
			return isBoundary;
		}
	};
	public abstract boolean[] getBoundary(final int[] is);
}