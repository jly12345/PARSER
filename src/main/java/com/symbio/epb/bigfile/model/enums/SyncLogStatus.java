package com.symbio.epb.bigfile.model.enums;

public enum SyncLogStatus {
    ONGOING(0), COMPLETED(1), FAILD(2);
    private int status;

    private SyncLogStatus(int status) {
        this.status = status;
    }

    public int getValue() {
        return this.status;
    }
}
