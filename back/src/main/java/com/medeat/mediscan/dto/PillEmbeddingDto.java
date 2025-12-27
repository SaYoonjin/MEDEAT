package com.medeat.mediscan.dto;

public class PillEmbeddingDto {

    private Long itemSeq;
    private String itemName;
    private String entpName;
    private Integer dim;
    private String vectorJson;

    public Long getItemSeq() {
        return itemSeq;
    }

    public void setItemSeq(Long itemSeq) {
        this.itemSeq = itemSeq;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getEntpName() {
        return entpName;
    }

    public void setEntpName(String entpName) {
        this.entpName = entpName;
    }

    public Integer getDim() {
        return dim;
    }

    public void setDim(Integer dim) {
        this.dim = dim;
    }

    public String getVectorJson() {
        return vectorJson;
    }

    public void setVectorJson(String vectorJson) {
        this.vectorJson = vectorJson;
    }
}
