package com.symbio.epb.bigfile.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.symbio.epb.bigfile.mapper.DomainLobSiteMapper;
import com.symbio.epb.bigfile.mapper.ParseLogMapper;
import com.symbio.epb.bigfile.mapper.UploadDataSyncLogMapper;
import com.symbio.epb.bigfile.model.enums.ParseStatus;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.PageDataService;
/**
 * 
 * @author Yao Pan
 *
 */
@Service
public class PageDataServiceImpl implements PageDataService {
	@Autowired
	private DomainLobSiteMapper domainLobSiteMapper;
	@Autowired
	private ParseLogMapper parseLogMapper;
	@Autowired
	private UploadDataSyncLogMapper uploadDataSyncLogMapper;
	@Override
	public List<DomainLobSite> findAllDomainLobSite() {
		return domainLobSiteMapper.findAll();
	}
	
	@Override
	public List<BigFileParseLog> findParseLogByType(int fileType) {
		return parseLogMapper.findParseLogByType(fileType);
	}

	@Override
	public List<UploadDataSyncLog> findUploadDataSyncLog(long parseLogId) {
		return uploadDataSyncLogMapper.findUploadDataSyncLogByParseLogId(parseLogId);
	}

	@Override
	public void deleteParseLogById(long parseLogId) {
		BigFileParseLog bigFileParseLog = new BigFileParseLog();
		bigFileParseLog.setId(parseLogId);
		bigFileParseLog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		bigFileParseLog.setStatus(ParseStatus.DELETED.getValue());
		parseLogMapper.updateStatusById(bigFileParseLog);
	}

}
