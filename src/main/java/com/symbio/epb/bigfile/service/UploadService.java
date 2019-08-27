package com.symbio.epb.bigfile.service;

import com.symbio.epb.bigfile.pojo.RequestResult;
import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
/**
 * 
 * @author Yao Pan
 *
 */
public interface UploadService {

	RequestResult uploadFile(UploadDataSyncLog uploadFileDataDto);

}
