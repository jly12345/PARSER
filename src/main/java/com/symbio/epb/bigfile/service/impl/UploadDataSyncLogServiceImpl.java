package com.symbio.epb.bigfile.service.impl;

import java.sql.Timestamp;
import java.util.Date;
//import javax.transaction.Transactional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.symbio.epb.bigfile.mapper.UploadDataSyncLogMapper;
import com.symbio.epb.bigfile.model.enums.SyncLogStatus;
import com.symbio.epb.bigfile.pojo.RequestResult;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.UploadDataSyncLogService;

/**
 * The<code>Class  SiteDataSyncLogServiceImpl </code>
 *
 * @author benju.xie
 * @since 2018/9/12
 */
@Service
@Scope("prototype")
public class UploadDataSyncLogServiceImpl implements UploadDataSyncLogService {

   // @Autowired
   // private SiteDataSyncLogRepo siteDataSyncLogRepo;
	@Autowired
	private UploadDataSyncLogMapper siteDataSyncLogMapper;
    @Override
    public void save(UploadDataSyncLog siteDataSyncLog) {
       // siteDataSyncLogRepo.save(siteDataSyncLog);
    	siteDataSyncLogMapper.insert(siteDataSyncLog);
    }

    @Override
    public void deleteByDataDate(long dataDate) {
        //siteDataSyncLogRepo.deleteByDataDate(dataDate);
    }

    @Override
    public List<UploadDataSyncLog> getSyncResult(long parseLogId) {
        return  siteDataSyncLogMapper.findUnsuccessSyncLogByParseLogId(parseLogId);
      //  return siteDataSyncLogRepo.findByDataDate(dataDate);
    }

	@Override
	public void updateStatus(UploadDataSyncLog dataSyncLog) {
		// TODO Auto-generated method stub
		dataSyncLog.setUpdateTime(new Timestamp(new Date().getTime()));
		siteDataSyncLogMapper.updateStatusById(dataSyncLog);
	}

	@Override
	public void updateStatusAndComment(long id, int attemptNumber, RequestResult result) {
		UploadDataSyncLog dataSyncLog = new UploadDataSyncLog();
    	dataSyncLog.setId(id);
    	dataSyncLog.setAttemptNumber(attemptNumber);
    	dataSyncLog.setComment(result.getComment());
    	dataSyncLog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
    	dataSyncLog.setStatus(result.isSuccess() ? SyncLogStatus.COMPLETED.getValue() : SyncLogStatus.FAILD.getValue());
    	siteDataSyncLogMapper.updateStatusById(dataSyncLog);
		
	}


}
