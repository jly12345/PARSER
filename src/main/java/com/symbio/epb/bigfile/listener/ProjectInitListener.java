package com.symbio.epb.bigfile.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
@Component
public class ProjectInitListener implements ApplicationListener<ContextRefreshedEvent> {
	 private static Logger logger = LoggerFactory.getLogger(ProjectInitListener.class);
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		//防止重复执行。
        if(event.getApplicationContext().getParent() == null){
        	logger.info("----------------------------启动成功------------------------------------------------------------------");

        }
		
	}

}
