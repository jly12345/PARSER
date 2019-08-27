package com.symbio.epb.bigfile.model.enums;
/**
 * 
 * @author Yao Pan
 *
 */
public enum BigFileType {
    MASTER(0), SITE(1);
    private int type;

    private BigFileType(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }
}
