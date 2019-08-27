package com.symbio.epb.bigfile.model.enums;
/**
 * 
 * @author Yao Pan
 *
 */
public enum ParseStatus {
	COMPLETED(0), FAILD(1), DELETED(2);
    private int status;

    private ParseStatus(int status) {
        this.status = status;
    }

    public int getValue() {
        return this.status;
    }
}
