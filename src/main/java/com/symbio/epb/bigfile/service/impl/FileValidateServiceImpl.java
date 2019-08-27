package com.symbio.epb.bigfile.service.impl;

import com.symbio.epb.bigfile.mapper.ParseLogMapper;
import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.pojo.BaseResponse;
import com.symbio.epb.bigfile.pojo.BigFileParseLog;
import com.symbio.epb.bigfile.service.FileValidateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class FileValidateServiceImpl implements FileValidateService{
	public static Logger logger = LoggerFactory.getLogger(FileValidateServiceImpl.class);
	@Autowired
	private ParseLogMapper parseLogMapper;
    @Value("${epb.bigfile.kpi-file-pre}")
    private String kpiFilePre;
    @Value("${epb.bigfile.site-file-pre}")
    private String siteFilePre;
	@Override
	public boolean validate(String fileName, String fileDate, BigFileType fileType, BaseResponse baseResponse) {
		if (StringUtils.isEmpty(fileName)) {
			baseResponse.setStatus(BaseResponse.Status.FAILED);
    		baseResponse.setErrorMessage("The file must not be empty.");
    		return false;
		}
      	//检查文件格式
        String fileNamePattern = "";
		if (fileType == BigFileType.MASTER) {
			fileNamePattern = "^" +kpiFilePre+ "[0-9]{8}.xls[x]?";
		}
		if (fileType == BigFileType.SITE) {
			fileNamePattern = "^" +siteFilePre+ "[0-9]{8}.xls[x]?";
		}
		if(!Pattern.matches(fileNamePattern, fileName)) {
			baseResponse.setStatus(BaseResponse.Status.FAILED);
			baseResponse.setErrorMessage("The formate of file name is incorrect.");
			logger.info("The formate of file name is incorrect: "+ fileName);
			return false;
		}
    	//检查是否已经上传过该文件
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			long dataDate = sdf.parse(fileDate).getTime();
			Timestamp fileDateTime = new Timestamp(dataDate);
			List<BigFileParseLog> list = parseLogMapper.findParseLogByFileDate(fileDateTime,fileType.getValue());
			if (list!=null && list.size()>0) {
				baseResponse.setStatus(BaseResponse.Status.FAILED);
				baseResponse.setErrorMessage("You have uploaded a file of this date already!");
				logger.info("There is a upload record exists already");
				return false;
			}
		} catch (ParseException e) {
			logger.info("File date is not correct");
			baseResponse.setStatus(BaseResponse.Status.FAILED);
			baseResponse.setErrorMessage("File date is not correct.");
		}
		return true;
	}
	
}
