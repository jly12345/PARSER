package com.symbio.epb.bigfile.pojo;

/**
 * The<code>Class  BigFileParseVo </code>
 *
 * @author benju.xie
 * @since 2018/8/6
 */

public class BigFileParseVo {

    private String lobName;
    private String siteName;
    private String rosterFilePath;
    private String mostFilePath;
    private String odrepeatsFilePath;
    private String adjFilePath;
    private Long uploadTime;

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

    public String getRosterFilePath() {
        return rosterFilePath;
    }

    public void setRosterFilePath(String rosterFilePath) {
        this.rosterFilePath = rosterFilePath;
    }

    public String getMostFilePath() {
        return mostFilePath;
    }

    public void setMostFilePath(String mostFilePath) {
        this.mostFilePath = mostFilePath;
    }

    public String getOdrepeatsFilePath() {
        return odrepeatsFilePath;
    }

    public void setOdrepeatsFilePath(String odrepeatsFilePath) {
        this.odrepeatsFilePath = odrepeatsFilePath;
    }

    public String getAdjFilePath() {
        return adjFilePath;
    }

    public void setAdjFilePath(String adjFilePath) {
        this.adjFilePath = adjFilePath;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }
}
