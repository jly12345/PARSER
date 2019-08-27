package com.symbio.epb.bigfile.error;

/**
 * The<code>Class  DefaultError </code>
 *
 * @author benju.xie
 * @since 2018/8/13
 */
public enum DefaultError implements IError {
    SYSTEM_INTERNAL_ERROR("0000", "System Internal Error");

    String errorCode;
    String errorMessage;
    String zh_errorMessage;

    private DefaultError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    private DefaultError(String errorCode, String errorMessage, String zh_errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getNamespace() {
        return "SYS";
    }

    public String getErrorCode() {
        return "SYS." + this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getZh_errorMessage() {
        return this.zh_errorMessage;
    }
}