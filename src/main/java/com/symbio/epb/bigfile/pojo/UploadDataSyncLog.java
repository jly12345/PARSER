package com.symbio.epb.bigfile.pojo;

import com.symbio.epb.bigfile.model.enums.SyncLogStatus;

//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * The<code>Class  SiteDataSyncLog </code>
 *
 * @author benju.xie
 * @since 2018/9/12
 */
//@Entity
public class UploadDataSyncLog implements Serializable {
	private static final long serialVersionUID = 1L;
    private Long id;
    private String lobName;
    private String domainName;
    private String siteName;
    private String filePath;
    private String fileName;
    private Timestamp fileDate;
    private int type;//0-roster;1-performance;2-sitedata
    private Timestamp createTime;
    private Timestamp updateTime;
    private String uploader;
    private int status = SyncLogStatus.ONGOING.getValue();
    private int attemptNumber = 0;
    private long parseLogId;
    private String comment;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLobName() {
        return lobName;
    }

    public void setLobName(String lobName) {
        this.lobName = lobName;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

	public Timestamp getFileDate() {
		return fileDate;
	}

	public void setFileDate(Timestamp fileDate) {
		this.fileDate = fileDate;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getAttemptNumber() {
		return attemptNumber;
	}

	public void setAttemptNumber(int attemptNumber) {
		this.attemptNumber = attemptNumber;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public long getParseLogId() {
		return parseLogId;
	}

	public void setParseLogId(long parseLogId) {
		this.parseLogId = parseLogId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
