package com.symbio.epb.bigfile.job;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.symbio.epb.bigfile.pojo.RequestResult;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.UploadDataSyncLogService;
import com.symbio.epb.bigfile.service.UploadService;
/**
 * 
 * @author Yao Pan
 *	
 */
@Component
public class UploadFileJob implements Job {
	private static Logger logger = LoggerFactory.getLogger(UploadFileJob.class);
	@Autowired 
	private UploadDataSyncLogService uploadDataSyncLogService;
    @Resource
    private ApplicationContext applicationContext;
	@Value("${epb.bigfile.max-upload-attempt-number}")
	private int maxAttemptNumber;
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		Object parseLogId = jobDataMap.get("parseLogId");
		Object type = jobDataMap.get("type");
		logger.info("job execute parseId: "+parseLogId+"   date:"+new Date()+" type:"+type);
		if (parseLogId == null) {
			logger.info("ParseLogId is null");
			return;
		}
		List<UploadDataSyncLog> syncResultList = uploadDataSyncLogService.getSyncResult(Long.parseLong(parseLogId+""));
		//建线程池提高上传效率
		ExecutorService threadPool = Executors.newCachedThreadPool();
		for (UploadDataSyncLog uploadDataSyncLog : syncResultList) {
			UploadFileThread thread = new UploadFileThread(uploadDataSyncLog);
			threadPool.execute(thread);
		}
		threadPool.shutdown();
	}
	
	private class UploadFileThread extends Thread {
		private UploadDataSyncLog uploadDataSyncLog;
		public UploadFileThread(UploadDataSyncLog uploadDataSyncLog) {
			this.uploadDataSyncLog = uploadDataSyncLog;
		}
		@Override
		public void run() {
			//直接从context里取uploadService,多例
			UploadService uploadService = (UploadService)applicationContext.getBean("uploadService");
			RequestResult result = uploadService.uploadFile(uploadDataSyncLog);
			int attemptNumber = uploadDataSyncLog.getAttemptNumber();
			if (attemptNumber < maxAttemptNumber ) {
				uploadDataSyncLogService.updateStatusAndComment(uploadDataSyncLog.getId(), attemptNumber+1, result);
			}
		}
	}

}
