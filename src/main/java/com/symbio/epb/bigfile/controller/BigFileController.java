package com.symbio.epb.bigfile.controller;

import com.symbio.epb.bigfile.pojo.BaseResponse;
import com.symbio.epb.bigfile.pojo.VBigFileBucket;
import com.symbio.epb.bigfile.service.BigFileService;
import com.symbio.epb.bigfile.service.FileValidateService;
import com.symbio.epb.bigfile.service.ParseLogService;
import com.symbio.epb.bigfile.utils.MyFileUtil;
import com.symbio.epb.bigfile.utils.ZipUtil;
import com.symbio.epb.bigfile.validator.BigFileUploadValidator;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * The<code>Class  BigFIleUploadController </code>
 *
 * @author benju.xie
 * @since 2018/8/6
 */
@RestController
@RequestMapping("bigfile")
public class BigFileController {
    public static Logger logger = LoggerFactory.getLogger(BigFileController.class);
    public static final String OUTOFMEMORYERROR = "java.heap.space";
    @Value("${epb.bigfile.split-file-path}")
    private String splitFilePath;
//    @Value("${epb.bigfile.domain-lob-site-path}")
//    private String domainLobSitePath;
    @Value("${epb.bigfile.bak-file-path}")
    private String bakFileDirPath;
    @Autowired
    private BigFileUploadValidator bigFileUploadValidator;
    @Autowired
    private ParseLogService parseLogService;
    @Autowired
    private FileValidateService fileValidateService;

    @Autowired
    private BigFileService bigFileService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(bigFileUploadValidator);

    }
//    @Lookup
//    public BigFileParserHandler getBigFileParserHandler() {
//    	return null;
//    }
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    public BaseResponse fileUpload(@Valid VBigFileBucket filebucket) throws Exception {
        logger.info("Ready to upload big file...");

        BaseResponse result = new BaseResponse();
        long startTime = System.currentTimeMillis();
        InputStream is = filebucket.getFile().getInputStream();
        String fileName = filebucket.getFile().getOriginalFilename();
        String[] array = fileName.split("_");
        String fileDate = array[3].substring(0, array[3].indexOf(".xlsx"));
        File deleteFile =  new File(splitFilePath+File.separator+fileDate);
        if(deleteFile.exists()){
            MyFileUtil.delFolder(deleteFile.getPath());
        }
        try {
            logger.info("Start parser big file...");
            bigFileService.process(is,fileDate);
            logger.info("Parser finish ues time:" + (System.currentTimeMillis() - startTime));
        }catch (OutOfMemoryError error){
            result.setStatus(BaseResponse.Status.FAILED);
            result.setErrorCode(OUTOFMEMORYERROR);
            logger.error("OutOfMemoryError:",error);
            return result;
        }

        fileName = fileName.replace(".xlsx",".zip");
        ZipUtil.zip(splitFilePath+File.separator+fileDate,splitFilePath,fileName);
        Map<String, Object> data = new HashMap<>();
        data.put("fileName", fileName);
        result.setData(data);
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
            logger.error(e.getMessage(),e);
        } finally {
            IOUtils.closeQuietly(os);
            IOUtils.closeQuietly(bis);
        }

        return result;
    }

    public  boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + File.separator + tempList[i]);//先删除文件夹里面的文件
                delFolder(path + File.separator + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            String filePath = folderPath;
            filePath = filePath.toString();
            File fileDir = new File(filePath);
            fileDir.delete(); // 删除空文件夹
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }
    
 
    

}
