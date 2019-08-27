package com.symbio.epb.bigfile.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.symbio.epb.bigfile.listener.ExcelListener;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.service.BigFileService;
import com.symbio.epb.bigfile.service.PageDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class BigFileServiceImpl implements BigFileService {
    public static Logger logger = LoggerFactory.getLogger(BigFileServiceImpl.class);
    @Autowired
    private PageDataService domainLobSiteService;

    @Value("${epb.bigfile.split-file-path}")
    private String splitFilePath;

    @Override
    public void process(InputStream inputStream,String fileDate) {
        List<DomainLobSite> domainlobsiteList = domainLobSiteService.findAllDomainLobSite();
        if (domainlobsiteList==null || domainlobsiteList.size()==0) {
        	logger.info(" There is no domain_lob_site data.");
			return;
		}
        parseSheetData(domainlobsiteList,inputStream,fileDate);
    }


    private void parseSheetData(List<DomainLobSite> domainlobsiteList,InputStream inputStream,String fileDate) {
        ExcelListener excelListener = new ExcelListener(domainlobsiteList,fileDate,splitFilePath);
        ExcelReader excelReader = EasyExcelFactory.getReader(inputStream,excelListener);
        List<Sheet> sheets = excelReader.getSheets();
        for (Sheet sheet:sheets) {
            logger.info("begin parse "+ sheet.getSheetName());
            excelReader.read(sheet);
            logger.info("end parse "+ sheet.getSheetName());
        }
    }


}
