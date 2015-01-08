package com.github.hiroshi_cl.util;

import java.util.*;

public class UnicodeBlockName {

    private static final Set<String> union = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "arabic", "cjk", "bopomofo", "cyrillic",
            "devanagari", "ethiopic", "georgian", "greek", "hangul", "latin", "myanmar", "canadian")));
    private static final Set<String> reject = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            "forms", "ipa", "symbol", "phonetic", "letter",
            "scripts", "punctuation")));

    public static boolean isSpecial(final char c) {
        if (!Character.isDefined(c) || Character.isSurrogate(c) || !Character.isLetter(c))
            return true;
        String name = Character.UnicodeBlock.of(c).toString().toLowerCase();
        for (final String s : reject)
            if (name.contains(s))
                return true;
        return false;
    }

    public static String getName(final char c, final boolean detail) {
        if (detail)
            return Character.UnicodeBlock.of(c).toString().toLowerCase();
        if (Character.isSurrogate(c))
            return "surrogate";
        if (isSpecial(c))
            return "";
        final String name = Character.UnicodeBlock.of(c).toString().toLowerCase();
        if (name.equals("hiragana") || name.equals("katakana"))
            return "kana";
        for (final String s : union)
            if (name.contains(s))
                return s;
        return name;
    }

    public static boolean notSeparated(final int c) {
        final String name = Character.UnicodeBlock.of(c).toString().toLowerCase();
        return name.startsWith("cjk") || name.startsWith("hira") || name.startsWith("kata") || name.startsWith("khmer")
                || name.startsWith("lao") || name.startsWith("thai") || name.startsWith("tib")
                || name.startsWith("yi");
    }

    public static Map<String, Integer> counts(final String s, final boolean detail) {
        final Map<String, Integer> map = new HashMap<>();
        for (final char c : s.toCharArray()) {
            final String name = getName(c, detail);
            if (!name.isEmpty()) {
                if (!map.containsKey(name))
                    map.put(name, 0);
                map.put(name, map.get(name) + 1);
            }
        }
        if (map.containsKey("kana")) {
            if (!map.containsKey("cjk"))
                map.put("cjk", 0);
            map.put("cjk_plus_kana", map.remove("cjk") + map.remove("kana"));
        }
        if (map.containsKey("surrogate"))
            map.put("surrogate", map.get("surrogate") / 2);
        return Collections.unmodifiableMap(map);
    }

    public static String mostFrequent(final String s, final boolean detail) {
        final Map<String, Integer> map = counts(s, detail);
        int max = 0;
        String ret = "";
        for (final String name : map.keySet())
            if (map.get(name) > max) {
                max = map.get(name);
                ret = name;
            }
        return ret;
    }
}
