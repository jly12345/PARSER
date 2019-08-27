package com.symbio.epb.bigfile.controller;

import com.symbio.epb.bigfile.pojo.BaseResponse;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.pojo.DomainLobSite;
import com.symbio.epb.bigfile.pojo.ListResponse;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.PageDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * The<code>Class  DataController </code>
 *
 * @author benju.xie
 * @since 2018/9/25
 */

@RestController
@RequestMapping("data")
public class DataController {

    public static Logger logger = LoggerFactory.getLogger(DataController.class);

    @Value("${epb.bigfile.domain-lob-site-path}")
    private String domainLobSitePath;
    @Value("${epb.bigfile.site-file-path}")
    private String siteSplitFilePath;
    @Value("${epb.bigfile.split-file-path}")
    private String bigFileSplitFilePath;
    @Value("${epb.bigfile.kpi-file-pre}")
    private String kpiFilePre;
    @Value("${epb.bigfile.site-file-pre}")
    private String siteFilePre;
    private List<DomainLobSite> domainLobSiteList;
    @Autowired
    private PageDataService pageDataService;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/getDomainLobSiteMapping", method = RequestMethod.GET)
    public ListResponse getDomainLobSiteMapping(){
        ListResponse response = new ListResponse();
//        loadLobSiteMapping();
        domainLobSiteList = pageDataService.findAllDomainLobSite();
        response.setResults(domainLobSiteList);
        return response;
    }
    
    @RequestMapping(value = "/getDomanLobSiteList", method = RequestMethod.GET)
    public List<DomainLobSite> getDomanLobSiteList(){
    	return pageDataService.findAllDomainLobSite();
    }
    
    @RequestMapping(value = "/getMasterParserHistory", method = RequestMethod.GET)
    public List<BigFileParseLog> getMasterParserHistory(int fileType){
    	return pageDataService.findParseLogByType(fileType);
    }
    
    @RequestMapping(value = "/getAutoUploadDetail", method = RequestMethod.GET)
    public List<UploadDataSyncLog> getAutoUploadDetail(long parseLogId){
    	return pageDataService.findUploadDataSyncLog(parseLogId);
    }
    
    @RequestMapping(value = "/deleteUploadDetail", method = RequestMethod.PUT)
    public void deleteUploadDetail(long parseLogId){
    	pageDataService.deleteParseLogById(parseLogId);
    }
    
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public  BaseResponse fileDownload(String fileName,HttpServletResponse res) {
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        BaseResponse result = new BaseResponse();
        if (StringUtils.isEmpty(fileName)) {
        	result.setStatus(BaseResponse.Status.FAILED);
        	return result;
		} 
        String splitFilePath = "";
        if (fileName.contains(kpiFilePre)) {
        	splitFilePath = bigFileSplitFilePath;
		} else if (fileName.contains(siteFilePre)) {
			splitFilePath = siteSplitFilePath;
		} else {
			result.setStatus(BaseResponse.Status.FAILED);
        	return result;
		}
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
			File zipFile = new File(splitFilePath+File.separator+ fileName);
            os = res.getOutputStream();
            bis = new BufferedInputStream(new FileInputStream(zipFile));
            int i = bis.read(buff);
            while (i != -1) {
                os.write(buff, 0, buff.length);
                os.flush();
                i = bis.read(buff);
            }
        } catch (IOException e) {
            result.setStatus(BaseResponse.Status.FAILED);
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    result.setStatus(BaseResponse.Status.FAILED);
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

}
