package com.symbio.epb.bigfile.service.impl;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.symbio.epb.bigfile.job.UploadFileJob;
import com.symbio.epb.bigfile.model.enums.ParseFileType;
import com.symbio.epb.bigfile.service.MasterFileUploadService;
/**
 * 
 * @author Yao Pan
 *
 */

@Service
public class MasterFileUploadServiceImpl implements MasterFileUploadService {
	private static Logger logger = LoggerFactory.getLogger(MasterFileUploadServiceImpl.class);
	@Value("${epb.bigfile.max-upload-attempt-number}")
	private int maxAttemptNumber;

	@Autowired
	private Scheduler defaultScheduler;
	@Override
	public void processMasterFile(long parseLogId, boolean needRoster) {
		try {
			//生成调度器
			JobDetail jobDetail = JobBuilder.newJob(UploadFileJob.class).withIdentity("trigger_roster", "group_roster").build();
			JobDataMap jobDataMap = jobDetail.getJobDataMap();
			jobDataMap.put("parseLogId", parseLogId);
			jobDataMap.put("type", ParseFileType.ROSTER.getValue());
			JobDetail perJobDetail = JobBuilder.newJob(UploadFileJob.class).withIdentity("trigger_perfms", "group_perfms").build();
			JobDataMap perJobDataMap = perJobDetail.getJobDataMap();
			perJobDataMap.put("parseLogId", parseLogId);
			perJobDataMap.put("type", ParseFileType.PERFORMANCE.getValue());
			if (needRoster) {
				//roster上传触发器
				SimpleTrigger rosterTrigger  = TriggerBuilder.newTrigger()
						.withIdentity(TriggerKey.triggerKey("trigger_roster","group_roster"))
						.withSchedule(SimpleScheduleBuilder.simpleSchedule()
								.withRepeatCount(maxAttemptNumber)
								.withIntervalInSeconds(10))
						.startNow()
						.build();
				//performance上传触发器
				SimpleTrigger performanceTrigger  = TriggerBuilder.newTrigger()
						.withIdentity(TriggerKey.triggerKey("trigger_perfms","group_perfms"))
						.withSchedule(SimpleScheduleBuilder.simpleSchedule()
								.withRepeatCount(maxAttemptNumber)
								.withIntervalInSeconds(10))
						.startAt(new Date(System.currentTimeMillis() + 10 * 1000))//10秒之后开始，roster上传需要一段时间
						.build();
				//添加调度器
				defaultScheduler.scheduleJob(jobDetail, rosterTrigger);
				defaultScheduler.scheduleJob(perJobDetail, performanceTrigger);
				//执行调度任务
				defaultScheduler.start();
			} else {
				//site上传触发器，不需要上传roster
				SimpleTrigger siteTrigger  = TriggerBuilder.newTrigger()
						.withIdentity(TriggerKey.triggerKey("trigger_site","group_site"))
						.withSchedule(SimpleScheduleBuilder.simpleSchedule()
								.withRepeatCount(maxAttemptNumber)
								.withIntervalInSeconds(10))
						.startNow()
						.build();
				//添加调度器
				defaultScheduler.scheduleJob(perJobDetail, siteTrigger);
				//执行调度任务
				defaultScheduler.start();
			}
		} catch (SchedulerException e) {
			logger.info("Do scheduler work error: "+ e.getMessage());
		} 
	}

}
