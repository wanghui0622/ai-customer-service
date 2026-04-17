package com.aics.rag.math;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VectorMathTest {

    @Test
    void identicalVectorsHaveCosineOne() {
        float[] v = {1f, 0f, 0f};
        assertThat(VectorMath.cosineSimilarity(v, v)).isEqualTo(1.0);
    }

    @Test
    void orthogonalVectorsHaveCosineZero() {
        float[] a = {1f, 0f};
        float[] b = {0f, 1f};
        assertThat(VectorMath.cosineSimilarity(a, b)).isEqualTo(0.0);
    }

    @Test
    void dimensionMismatchThrows() {
        assertThatThrownBy(() -> VectorMath.cosineSimilarity(new float[]{1f}, new float[]{1f, 0f}))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
