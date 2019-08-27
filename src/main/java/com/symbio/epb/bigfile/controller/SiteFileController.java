package com.symbio.epb.bigfile.controller;

import com.symbio.epb.bigfile.handler.SiteFilePaserHandler;
import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.model.enums.ParseStatus;
import com.symbio.epb.bigfile.pojo.BaseResponse;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.pojo.VSiteFileBucket;
import com.symbio.epb.bigfile.service.FileValidateService;
import com.symbio.epb.bigfile.service.ParseLogService;
import com.symbio.epb.bigfile.utils.MyFileUtil;
import com.symbio.epb.bigfile.utils.ZipUtil;
import com.symbio.epb.bigfile.validator.SiteFileUploadValidator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * The<code>Class  SiteFileController </code>
 *
 * @author benju.xie
 * @since 2018/9/4
 */

@RestController
@RequestMapping("sitefile")
public class SiteFileController {
    public static Logger logger = LoggerFactory.getLogger(SiteFileController.class);

    @Value("${epb.bigfile.site-file-path}")
    private String splitFilePath;
    @Value("${epb.bigfile.domain-lob-site-path}")
    private String domainLobSitePath;
    @Value("${epb.bigfile.bak-file-path}")
    private String bakFileDirPath;
    @Autowired
    private ParseLogService parseLogService;
    @Autowired
    private SiteFileUploadValidator siteFileUploadValidator;
    @Autowired
    private FileValidateService fileValidateService;



    @Lookup
    public SiteFilePaserHandler getSiteFilePaserHandler() {
    	return null;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(siteFileUploadValidator);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public BaseResponse fileUpload(@Valid VSiteFileBucket filebucket) throws Exception {
        logger.info("Ready to upload site file...");
        long startTime = System.currentTimeMillis();
        InputStream is = filebucket.getFile().getInputStream();
        String fileName = filebucket.getFile().getOriginalFilename();
        String[] array = fileName.split("_");
        String fileDate = array[3].substring(0, array[3].indexOf(".xlsx"));
        File deleteFile =  new File(splitFilePath+File.separator+fileDate);
        if(deleteFile.exists()){
            MyFileUtil.delFolder(deleteFile.getPath());
        }
        SiteFilePaserHandler handler = getSiteFilePaserHandler();
//        handler.setDomainLobSitePath(domainLobSitePath);
        handler.setFileDate(fileDate);
//        handler.setSplitFilePath(splitFilePath);
        logger.info("Start parser site file...");
        handler.process(is);
        logger.info("Parser finish ues time:"+(System.currentTimeMillis()-startTime));
        BaseResponse result = new BaseResponse();

        if(handler.getErrMsg().length()>0){
            result.setStatus(BaseResponse.Status.FAILED);
            result.setErrorMessage(handler.getErrMsg().toString());
        }else {
            fileName = fileName.replace(".xlsx",".zip");
            ZipUtil.zip(splitFilePath+File.separator+fileDate,splitFilePath,fileName);
            Map<String, Object> data = new HashMap<>();
            data.put("fileName", fileName);
            result.setData(data);
        }
        return result;
    }
    
    @RequestMapping(value = "autoUploadFile", method = RequestMethod.POST)
    public BaseResponse fileAutoUpload(@Valid VSiteFileBucket filebucket){
    	logger.info("Ready to upload site file...");
    	long startTime = System.currentTimeMillis();
    	String fileName = filebucket.getFile().getOriginalFilename();
    	BaseResponse result = new BaseResponse();
    	String[] array = fileName.split("_");
    	String fileDate = array[3].substring(0, array[3].indexOf(".xlsx"));
    	if (!fileValidateService.validate(fileName, fileDate, BigFileType.SITE, result)) {
    		return result;
		}
    	File deleteFile =  new File(splitFilePath+File.separator+fileDate);
    	if(deleteFile.exists()){
    		MyFileUtil.delFolder(deleteFile.getPath());
    	}
    	SiteFilePaserHandler handler = getSiteFilePaserHandler();
    	//save bak file
    	BigFileParseLog parseLog = parseLogService.createLog(fileName, BigFileType.SITE, fileDate, startTime);
    	handler.setFileDate(fileDate);
    	handler.setParseLogId(parseLog.getId());
    	logger.info("Start parser site file...");
    	InputStream is = null;
    	try {
    		is = filebucket.getFile().getInputStream();
	    	handler.processUpload(is);
	    	long endTime = System.currentTimeMillis();
			logger.info("Parser finish ues time:" + (endTime - startTime));
			if(handler.getErrMsg().length()>0){
				result.setStatus(BaseResponse.Status.FAILED);
				result.setErrorMessage(handler.getErrMsg().toString());
				parseLogService.updateLogStatus(parseLog, endTime, ParseStatus.FAILD);
			}else {
				parseLogService.updateLogStatus(parseLog, endTime, ParseStatus.COMPLETED);
				fileName = fileName.replace(".xlsx",".zip");
				ZipUtil.zip(splitFilePath+File.separator+fileDate,splitFilePath,fileName);
				Map<String, Object> data = new HashMap<>();
				data.put("fileName", fileName);
				result.setData(data);
			}
		} catch (Exception e) {
			logger.error("Parse error: " + e.getMessage());
			parseLogService.updateLogStatus(parseLog, System.currentTimeMillis(), ParseStatus.FAILD);
		} finally {
            IOUtils.closeQuietly(is);
		}
    	return result;
    }
    

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public  BaseResponse fileDownload(String fileName,HttpServletResponse res) {
        res.setHeader("content-type", "application/octet-stream");
        res.setContentType("application/octet-stream");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] buff = new byte[1024];
        BufferedInputStream bis = null;
        OutputStream os = null;
        BaseResponse result = new BaseResponse();
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
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(os);
        }

        return result;
    }

//    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
//    public ListResponse<SiteDataSyncLog> getResult(String dataDate){
//        ListResponse<SiteDataSyncLog> response = new ListResponse<>();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
////        try {
////            response.setResults( siteDataSyncLogService.getSyncResult( sdf.parse(dataDate).getTime()));
////        } catch (ParseException e) {
////            e.printStackTrace();
////        }
//
//        return response;
//    }

}
