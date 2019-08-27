package com.symbio.epb.bigfile.pojo;
/**
 * 
 * @author Yao Pan
 *
 */
public class RequestResult {
	private boolean isSuccess;
	private String comment;
	
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
