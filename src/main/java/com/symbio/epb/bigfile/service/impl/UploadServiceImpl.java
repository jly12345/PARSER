package com.symbio.epb.bigfile.service.impl;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.symbio.epb.bigfile.model.enums.ParseFileType;
import com.symbio.epb.bigfile.pojo.RequestResult;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
import com.symbio.epb.bigfile.service.UploadService;
import com.symbio.epb.bigfile.utils.JSONUtil;
import com.symbio.epb.bigfile.utils.MyStringUtils;
/**
 * 
 * @author Yao Pan
 *
 */
@Service("uploadService")
@Scope("prototype")
public class UploadServiceImpl implements UploadService{
	@Value("${spring.profiles.active}")
	private String profile;
	private static String PROFILE_DEV   = "dev";
	private static String PROFILE_AWS   = "aws";
	private static String PROFILE_VXI   = "vxi";
	private static int    TYPE_LOGIN    = -1;
	@Value("${epb.bigfile.uploader-password}")
	private String password;
	@Autowired
	private RestTemplate restTemplate;
    public static Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);
	@Override
	@SuppressWarnings("unchecked")
	public RequestResult uploadFile(UploadDataSyncLog uploadFileDataDto) {
		HttpHeaders headers = new HttpHeaders();
		RequestResult requestResult = login(uploadFileDataDto);
		if (requestResult.isSuccess()) {
			// login success, to upload file
			LinkedMultiValueMap<String, Object> body = buildRequestBody(uploadFileDataDto);
			URI url = null;
			String urlStr = buildUrl(uploadFileDataDto.getDomainName(), uploadFileDataDto.getType());
			try {
				url = new URI(urlStr);
			} catch (URISyntaxException e) {
				logger.error("Upload file URI is not correct -----> "+ urlStr);
				RequestResult result = new RequestResult();
				result.setComment("Upload URI is incorrect: "+urlStr);
				result.setSuccess(false);
				return result;
			}
			FileSystemResource resource = new FileSystemResource(new File(uploadFileDataDto.getFilePath()));
			body.add("file", resource);
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			//浏览器（chrome）当前显示的user-agent
	        String myUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
	        headers.add(HttpHeaders.USER_AGENT, myUserAgent);
			HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers);
			ResponseEntity<String> responseEntity = null;
			try {
				responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
				//报302错重试
				if (responseEntity != null && responseEntity.getStatusCodeValue()== HttpStatus.FOUND.value()) {
					requestResult = login(uploadFileDataDto);
					if (requestResult.isSuccess()) {
						responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
					}
				} 
			} catch (RestClientResponseException e) {
				//重复上传会报HttpClientErrorException ;kpi计算出错会报HttpServerErrorException
				String responseBodyAsString = e.getResponseBodyAsString();
				Map<String, Object> resopnseMap =  (Map<String, Object>)JSONUtil.formatStr2Object(responseBodyAsString, Map.class);
				requestResult.setSuccess(false);
				requestResult.setComment(resopnseMap.get("message")+"");
				return requestResult;
			} catch (Exception e) {
				requestResult.setSuccess(false);
				requestResult.setComment(e.toString());
				
			}
			return isRequestSuccess(responseEntity);
		}
		return requestResult;
	}

	public RequestResult login(UploadDataSyncLog uploadFileDataDto){
		HttpHeaders headers = new HttpHeaders();
		String urlStr = buildUrl(uploadFileDataDto.getDomainName(), TYPE_LOGIN);
		URI url = null;
		RequestResult result = new RequestResult();
		ResponseEntity<String> responseEntity = null;
		try {
			url = new URI(urlStr);
			LinkedMultiValueMap<String, Object> body = buildRequestBody(uploadFileDataDto);
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			//浏览器（chrome）当前显示的user-agent
	        String myUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
	        headers.add(HttpHeaders.USER_AGENT, myUserAgent);
			HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		} catch (URISyntaxException e) {
			logger.error("Login URI is not correct -----> "+ urlStr);
			result.setComment("Login URI is incorrect: "+urlStr);
			result.setSuccess(false);
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage());
			result.setComment(e.getMessage());
			result.setSuccess(false);
			return result;
		}
		result = isRequestSuccess(responseEntity);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private RequestResult isRequestSuccess(ResponseEntity<String> responseEntity) {
		RequestResult result = new RequestResult();
		Map<String, Object> responseMap = (Map<String, Object>) JSONUtil.formatStr2Object(responseEntity.getBody(),Map.class);
		boolean isSuccess =  responseEntity!=null 
				&& responseEntity.getStatusCode().equals(HttpStatus.OK);
		
		result.setSuccess(isSuccess);
		String comment = null;
		if (isSuccess) {
			comment =  "upload success" ;
		} else {
			comment = responseMap!=null ? responseMap.get("message")+"" : responseEntity.toString();
		}
		result.setComment(comment);
		return result;
		
	}
	
	private LinkedMultiValueMap<String, Object> buildRequestBody(UploadDataSyncLog uploadFileDataDto) {
		LinkedMultiValueMap<String, Object> body=new LinkedMultiValueMap<String,Object>();
		String site = uploadFileDataDto.getSiteName();
		String lob = uploadFileDataDto.getLobName();
        body.add("username", uploadFileDataDto.getUploader());
        body.add("password", password);
        body.add("site", "ds" + MyStringUtils.toUpperCaseFirstOne(lob) + MyStringUtils.toUpperCaseFirstOne(site));
        body.add("lob", lob);
		return body;
        
	}
	
	private String buildUrl(String domain, int type) {
		if (PROFILE_DEV.equals(profile)) {
			domain = "http://localhost:8080";
		}
		if (PROFILE_VXI.equals(profile)) {
			if (domain.startsWith("ppro360")) {
				//vxi的staging domain是单独一个，aws的staging domain分很多个
				domain = "https://staging.ppro360.com";
			} else {
				String regex = "(?=\\.)";//匹配点号的位置
				Pattern pattern = Pattern.compile(regex);
				Matcher  ma = pattern.matcher(domain);
				domain = "https://" + ma.replaceFirst("-staging");//例如iqor.symbio.com转换成iqor-staging.symbio.com
			}
		}
		if (PROFILE_AWS.equals(profile)) {
			domain = "https://" + domain;
		}
		
		if (type == TYPE_LOGIN) {
			domain = domain + "/ajax_security_check";
		}
		if (type == ParseFileType.PERFORMANCE.getValue()|| type == ParseFileType.SITE.getValue()) {
			domain = domain + "/Admin/PerformanceUpload";
		}
		if (type == ParseFileType.ROSTER.getValue()) {
			domain = domain + "/Admin//RosterUpload";
		}
		return domain;
	}

}
