package com.medeat.mediscan.embedding;

public class EmbeddingItem {

    private Long itemSeq;
    private int dim;
    private float[] embedding;

    // 검색 시 계산되는 점수 (런타임용)
    private double score;

    public EmbeddingItem() {}

    public EmbeddingItem(Long itemSeq, int dim, float[] embedding) {
        this.itemSeq = itemSeq;
        this.dim = dim;
        this.embedding = embedding;
    }

    public Long getItemSeq() {
        return itemSeq;
    }

    public void setItemSeq(Long itemSeq) {
        this.itemSeq = itemSeq;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
