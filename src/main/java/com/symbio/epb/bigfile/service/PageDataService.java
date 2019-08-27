package com.symbio.epb.bigfile.service;

import java.util.List;

import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;

public interface PageDataService {
	
	List<DomainLobSite> findAllDomainLobSite();
	
	List<BigFileParseLog> findParseLogByType(int fileType);
	
	List<UploadDataSyncLog> findUploadDataSyncLog(long parseLogId);
	
	void deleteParseLogById(long parseLogId);
}
