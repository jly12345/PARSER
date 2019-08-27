package com.symbio.epb.bigfile.handler;

import com.symbio.epb.bigfile.event.ParseCompleteEvent;
import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.service.MasterFileUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 
 * @author Yao Pan
 *	
 *	监听拆分完成事件，开始上传拆分后的文件任务
 */
@Component
public class ParseCompleteEventHandler {
	private static Logger logger = LoggerFactory.getLogger(ParseCompleteEventHandler.class);
	@Autowired
	private MasterFileUploadService uploadService;
	@SuppressWarnings("unchecked")
	@EventListener
	public void listener(ParseCompleteEvent event) {
		logger.info("Auto upload start... ");
		if (event == null ||event.getSource() == null) {
			logger.info("Upolad error, param is null.");
			return;
		}
		HashMap<String, Object> paramMap = (HashMap<String, Object>)event.getSource();
		Object fileType = paramMap.get("fileType");
		Object parseLogId = paramMap.get("parseLogId");
		if (Integer.parseInt(fileType+"") == BigFileType.MASTER.getValue()) {
			long parseIdLong = Long.parseLong(parseLogId+"");
			uploadService.processMasterFile(parseIdLong, true);
		}
		if (Integer.parseInt(fileType+"") == BigFileType.SITE.getValue()) {
			long parseIdLong = Long.parseLong(parseLogId+"");
			uploadService.processMasterFile(parseIdLong, false);
		}
	}
}
