package com.symbio.epb.bigfile.config;

import java.io.IOException;

import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.symbio.epb.bigfile.job.MyJobFactory;
/**
 * 
 * @author Yao Pan
 *	
 *	job context中得到spring context中的bean
 */

@Configuration
@EnableScheduling
public class QuartzScheduleConfig { 
 
	@Autowired 
	private MyJobFactory myJobFactory; 
 
	@Bean 
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {
	    SchedulerFactoryBean factory = new SchedulerFactoryBean();
	    factory.setOverwriteExistingJobs(true); 
	    // 延时启动，需要等待spring context先启动
	    factory.setStartupDelay(20); 
	    // 自定义Job Factory，用于Spring注入
	    factory.setJobFactory(myJobFactory);
	    return factory; 
	}
	@Bean
	@Primary
	public Scheduler scheduler() throws IOException {
	    return schedulerFactoryBean().getScheduler();
	}
}
