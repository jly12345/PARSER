package com.symbio.epb.bigfile.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.symbio.epb.bigfile.mapper.ParseLogMapper;
import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.model.enums.ParseStatus;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.service.ParseLogService;
@Service
public class ParseLogServiceImpl implements ParseLogService {
	public static Logger logger = LoggerFactory.getLogger(ParseLogServiceImpl.class);
	@Value("${epb.bigfile.bak-file-path}")
	private String bakFileDirPath;
	@Autowired
	private ParseLogMapper parseLogMapper;
	@Override
	public BigFileParseLog createLog(String fileName, BigFileType type, String fileDate, Long startTime) {
    	BigFileParseLog bigFileParseLog = new BigFileParseLog();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Long dataDate = null;
		try {
			dataDate = sdf.parse(fileDate).getTime();
		} catch (ParseException e) {
			logger.error("The date of file is not match the forma: "+ sdf.toPattern());
		}
    	bigFileParseLog.setFileDate(new Timestamp(dataDate));
    	bigFileParseLog.setFileName(fileName);
    	bigFileParseLog.setFilePath(bakFileDirPath+fileName);
    	bigFileParseLog.setFileType(type.getValue());
    	bigFileParseLog.setStartTime(new Timestamp(startTime));
    	parseLogMapper.insert(bigFileParseLog);
		return bigFileParseLog;
	}
	
	@Override
	public void updateLogStatus(BigFileParseLog parseLog, Long endTime, ParseStatus status) {
		parseLog.setEndTime(new Timestamp(endTime));
		parseLog.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		parseLog.setStatus(status.getValue());
		parseLogMapper.updateStatusAndEndTimeById(parseLog);
		
	}

}
