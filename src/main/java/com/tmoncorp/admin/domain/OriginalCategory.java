package com.tmoncorp.admin.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "original_category")
public class OriginalCategory {

    @Id
    private int catNo;

    private int parentCatNo;
    private String catNm;
    private int vwOrder;
    private int depth;

    public OriginalCategory() {

    }

    public OriginalCategory(int catNo, int parentCatNo, String catNm, int vwOrder, int depth) {
        this.catNo = catNo;
        this.parentCatNo = parentCatNo;
        this.catNm = catNm;
        this.vwOrder = vwOrder;
        this.depth = depth;
    }

    public void setCatNo(int catNo) {
        this.catNo = catNo;
    }

    public void setParentCatNo(int parentCatNo) {
        this.parentCatNo = parentCatNo;
    }

    public void setCatNm(String catNm) {
        this.catNm = catNm;
    }

    public void setVwOrder(int vmOrder) {
        this.vwOrder = vwOrder;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getCatNo() {
        return catNo;
    }

    public int getParentCatNo() {
        return parentCatNo;
    }

    public String getCatNm() {
        return catNm;
    }

    public int getVwOrder() {
        return vwOrder;
    }

    public int getDepth() {
        return depth;
    }
}
