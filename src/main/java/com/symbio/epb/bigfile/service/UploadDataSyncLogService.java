package com.symbio.epb.bigfile.service;

import java.util.List;

import com.symbio.epb.bigfile.pojo.RequestResult;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;

/**
 * The<code>Class  SiteDataSyncLogService </code>
 *
 * @author benju.xie
 * @since 2018/9/12
 */
public interface UploadDataSyncLogService {
    void save(UploadDataSyncLog siteDataSyncLog);
    void deleteByDataDate(long dataDate);
    List<UploadDataSyncLog> getSyncResult(long parseLogId);
    void updateStatus(UploadDataSyncLog siteDataSyncLog);
    void updateStatusAndComment(long id, int attemptNumber, RequestResult result);

}
