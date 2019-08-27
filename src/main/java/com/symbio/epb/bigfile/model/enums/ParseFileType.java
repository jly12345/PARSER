package com.symbio.epb.bigfile.model.enums;

public enum ParseFileType {
    ROSTER(0), PERFORMANCE(1),SITE(2);
    private int type;

    private ParseFileType(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }
}
