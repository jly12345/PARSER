package com.symbio.epb.bigfile.service;

import com.symbio.epb.bigfile.model.enums.BigFileType;
import com.symbio.epb.bigfile.pojo.BaseResponse;
/**
 * 
 * @author Yao Pan
 *
 */

public interface FileValidateService {
	boolean validate(String fileName, String fileDate, BigFileType fileType, BaseResponse baseResponse);
}
