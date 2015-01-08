package com.github.hiroshi_cl.seglang;

import com.github.hiroshi_cl.seglang.compressors2.*;
import com.github.hiroshi_cl.seglang.segmenters.*;

import java.util.ArrayList;
import java.util.List;

public enum Segmenters {
    Proposal {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings, Object... params) {
            final Compressors method = (Compressors) params[0];
            final double gamma = (double) params[1];
            final Compressor[] comps = method.getCompressors(trainings);
            return method == Compressors.MMS ? new MMSSeg(names, (MMS[]) comps, gamma) : new PPMSeg(names,
                    (PPM[]) comps, gamma);
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                for (final double gamma : new double[]{16. * s, 64.})
                    list.add(new Object[]{method, gamma});
            return list.toArray(new Object[0][]);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                for (final double gamma : new double[]{16. * s, 64.})
                    list.add(String.format("Proposal: %s %.1f", method, gamma));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                list.add(new Object[]{method, 32.});
            return list.toArray(new Object[0][]);
        }

    },
    WAC {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings,
                                      Object... params) {
            final Compressors method = (Compressors) params[0];
            final double gamma = (double) params[1];
            final Compressor[] comps = method.getCompressors(trainings);
            return method == Compressors.MMS ? new MMSSegWAC(names, (MMS[]) comps,
                    gamma) : new PPMSegWAC(names,
                    (PPM[]) comps, gamma);
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            return Proposal.enumParams(alphabetSize);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                for (final double gamma : ds)
                    list.add(String.format("WAC: %s %.1f", method, gamma));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(new Object[]{method, 32.});
            return list.toArray(new Object[0][]);
        }
    },
    Greedy {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings,
                                      Object... params) {
            return new Greedy(names, ((Compressors)
                    params[0]).getCompressors(trainings));
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(new Object[]{method});
            return list.toArray(new Object[0][]);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(String.format("Greedy: %s", method));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(new Object[]{method});
            return list.toArray(new Object[0][]);
        }
    },
    Individual {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings,
                                      Object... params) {
            return new Individual(names, ((Compressors)
                    params[0]).getCompressors(trainings));
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(new Object[]{method});
            return list.toArray(new Object[0][]);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(String.format("Individual: %s", method));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS,
                    Compressors.PPM})
                list.add(new Object[]{method});
            return list.toArray(new Object[0][]);
        }
    },
    Teahan {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings, Object... params) {
            final PPM[] ppms = new PPM[trainings.length];
            for (int i = 0; i < trainings.length; i++)
                ppms[i] = new PPM(trainings[i], PPMMethod.C, 5, (int) params[0]);
            return new Teahan(names, ppms);
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            final List<Object[]> list = new ArrayList<>();
            final int max = Character.MAX_CODE_POINT + 2;
            for (final int u : new int[]{max})
                list.add(new Object[]{u});
            return list.toArray(new Object[0][]);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final String s : new String[]{"ALL_CP"})
                list.add(String.format("Teahan: %s", s));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            return new Object[][]{new Object[]{Character.MAX_CODE_POINT + 2}};
        }
    },
    TeahanWAC {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings,
                                      Object... params) {
            final PPM[] ppms = new PPM[trainings.length];
            for (int i = 0; i < trainings.length; i++)
                ppms[i] = new PPM(trainings[i], PPMMethod.C, 5, (int) params[0]);
            return new TeahanWAC(names, ppms);
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            return Teahan.enumParams(alphabetSize);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final String s : new String[]{"MIN", "MED", "ALL_CP"})
                list.add(String.format("TeahanWAC: %s", s));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            return new Object[][]{new Object[]{Character.MAX_CODE_POINT + 2}};
        }
    },
    TextTiling {
        @Override
        public Segmenter getSegmenter(String[] names, String[] trainings, Object... params) {
            final Compressors method = (Compressors) params[0];
            return new TextTiling(names, new Identifier(names, trainings, method), method, (double) params[2],
                    (int) params[1], false);
        }

        @Override
        public Object[][] enumParams(final int alphabetSize) {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                for (final int length : new int[]{160})
                    for (final double threshold : new double[]{32., 128.})
                        list.add(new Object[]{method, length, threshold});
            return list.toArray(new Object[0][]);
        }

        @Override
        public String[] enumNames() {
            final List<String> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                for (final int length : new int[]{160})
                    for (final double threshold : new double[]{32., 128.})
                        list.add(String.format("TextTiling: %s %d %.1f", method, length, threshold));
            return list.toArray(new String[0]);
        }

        @Override
        public Object[][] enumParamsMini() {
            final List<Object[]> list = new ArrayList<>();
            for (final Compressors method : new Compressors[]{Compressors.MMS, Compressors.PPM})
                list.add(new Object[]{method, 40, 40.});
            return list.toArray(new Object[0][]);
        }
    };

    public abstract Segmenter getSegmenter(String[] names, final String[] trainings, final Object... params);

    public abstract Object[][] enumParams(final int alphabetSize);

    public abstract Object[][] enumParamsMini();

    public abstract String[] enumNames();

    private static final double s = Math.sqrt(2.);
    private static double[] ds = {0., 1., s, 2., 2. * s, 4., 4. * s, 8., 8. * s, 16., 16. * s, 32., 32. * s, 64.,
            64. * s, 128., 128. * s, 256.};
}
