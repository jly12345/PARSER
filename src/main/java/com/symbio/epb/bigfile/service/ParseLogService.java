package com.symbio.epb.bigfile.service;

import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.model.enums.ParseStatus;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
/**
 * 
 * @author Yao Pan
 *
 */
public interface ParseLogService {
	/**
	 * 
	 * @return the new created log
	 */
	BigFileParseLog createLog(String fileName, BigFileType type, String fileDate,  Long startTime);
	
	void updateLogStatus(BigFileParseLog parseLog, Long endTime, ParseStatus status);
	
}
