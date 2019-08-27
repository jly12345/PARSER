package com.symbio.epb.bigfile.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class BigFileParseLog implements Serializable{

	private static final long serialVersionUID = 1L;
	
    private Long id;
    private String fileName;
    private String filePath;
    private int fileType;
    private Timestamp fileDate;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp updateTime;//0- success; 1-failed; 2-deleted
    private int status = 0;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public Timestamp getFileDate() {
		return fileDate;
	}
	public void setFileDate(Timestamp fileDate) {
		this.fileDate = fileDate;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
