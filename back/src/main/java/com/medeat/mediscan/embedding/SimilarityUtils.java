package com.medeat.mediscan.embedding;

public class SimilarityUtils {

    private SimilarityUtils() {}

    public static double cosine(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) {
            return -1.0;
        }

        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        if (normA == 0 || normB == 0) {
            return -1.0;
        }

        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
