package com.symbio.epb.bigfile.error;

/**
 * The<code>Class  BaseBusinessModuleException </code>
 *
 * @author benju.xie
 * @since 2018/8/13
 */
public class BaseBusinessModuleException extends RuntimeException {
    private static final long serialVersionUID = -6293662498600553602L;
    protected IError error;
    protected String extMessage;

    public BaseBusinessModuleException() {
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
    }

    public BaseBusinessModuleException(String message) {
        super(message);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
    }

    public BaseBusinessModuleException(String message, Throwable cause) {
        super(message, cause);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
    }

    public BaseBusinessModuleException(Throwable cause) {
        super(cause);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
    }

    public BaseBusinessModuleException(IError error) {
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.error = error;
    }

    public BaseBusinessModuleException(String message, IError error) {
        super(message);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
        this.error = error;
    }

    public BaseBusinessModuleException(String message, Throwable cause, IError error) {
        super(message, cause);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.extMessage = message;
        this.error = error;
    }

    public BaseBusinessModuleException(Throwable cause, IError error) {
        super(cause);
        this.error = DefaultError.SYSTEM_INTERNAL_ERROR;
        this.extMessage = null;
        this.error = error;
    }

    public IError getError() {
        return this.error;
    }

    public String getExtMessage() {
        return this.extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public String toString() {
        return super.toString() + ",ErrorCode : " + this.error.getErrorCode() + ", ErrorMessage : " + this.error.getErrorMessage() + ", ExtMessage : " + this.extMessage;
    }
}
