package com.symbio.epb.bigfilet.test;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.symbio.epb.bigfile.Application;
import com.symbio.epb.bigfile.mapper.UploadDataSyncLogMapper;
import com.symbio.epb.bigfile.model.enums.SyncLogStatus;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class SiteDataSyncLogTest {
	@Autowired
	private UploadDataSyncLogMapper siteDataSyncLogMapper;
	@Test
	public void testInsert() {
		UploadDataSyncLog siteDataSyncLog = new UploadDataSyncLog();
		siteDataSyncLog.setCreateTime(new Timestamp(System.currentTimeMillis()));
		siteDataSyncLog.setFileDate(new Timestamp(System.currentTimeMillis()));
		siteDataSyncLog.setDomainName("domainName");
		siteDataSyncLog.setFilePath("filePath");
		siteDataSyncLog.setLobName("lobDomain");
		siteDataSyncLog.setSiteName("siteName");
//		siteDataSyncLog.setStatus(SyncLogStatus.ONGOING.getValue());
		siteDataSyncLog.setUploader("uploader");
		int id = siteDataSyncLogMapper.insert(siteDataSyncLog );
		System.out.println("siteDataSyncLog.getId()----"+siteDataSyncLog.getId());
		System.out.println("id--"+id);
	}
	
	@Test
	public void testUpdate() {
		UploadDataSyncLog siteDataSyncLog = new UploadDataSyncLog();
		siteDataSyncLog.setId(1l);
		siteDataSyncLog.setStatus(SyncLogStatus.FAILD.getValue());
		int i = siteDataSyncLogMapper.updateStatusById(siteDataSyncLog);
		System.out.println(i);
	}
}
