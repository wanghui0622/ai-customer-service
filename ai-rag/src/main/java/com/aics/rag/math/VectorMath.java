package com.aics.rag.math;

/**
 * 向量运算（教学用：余弦相似度）。
 */
public final class VectorMath {

    private VectorMath() {
    }

    /**
     * 余弦相似度，范围约 [-1, 1]，检索场景下通常只关心非负相关。
     */
    public static double cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("dimension mismatch: " + a.length + " vs " + b.length);
        }
        double dot = 0;
        double na = 0;
        double nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += (double) a[i] * b[i];
            na += (double) a[i] * a[i];
            nb += (double) b[i] * b[i];
        }
        if (na == 0.0 || nb == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
