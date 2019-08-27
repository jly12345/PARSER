package com.symbio.epb.bigfile.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.symbio.epb.bigfile.error.IError;

import java.io.Serializable;
import java.util.Map;

/**
 * The<code>Class  BaseResponse </code>
 *
 * @author benju.xie
 * @since 2018/8/13
 */
@SuppressWarnings("deprecation")
@JsonSerialize(
        include = JsonSerialize.Inclusion.NON_NULL
)
public class BaseResponse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode;
    private String errorMessage;
    private String extMessage;
    private BaseResponse.Status status;
    private Map<String,Object> data;

    public BaseResponse.Status getStatus() {
        return this.status;
    }

    public void setStatus(BaseResponse.Status status) {
        this.status = status;
    }

    public BaseResponse() {
        this.status = BaseResponse.Status.SUCCEED;
    }

    public BaseResponse(IError error) {
        this.status = BaseResponse.Status.SUCCEED;
        this.errorCode = error.getErrorCode();
        this.errorMessage = error.getErrorMessage();
        this.status = BaseResponse.Status.FAILED;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getExtMessage() {
        return this.extMessage;
    }

    public void setExtMessage(String extMessage) {
        this.extMessage = extMessage;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(this.errorCode != null) {
            sb.append("ErrorCode : ").append(this.errorCode).append("ErrorMessage : ").append(this.errorMessage).append("ExtMessage : " + this.extMessage);
        } else {
            sb.append("Succeed");
        }

        return sb.toString();
    }

    public static enum Status {
        SUCCEED,
        FAILED;

        private Status() {
        }
    }
}
