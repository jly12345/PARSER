package com.symbio.epb.bigfile.service;

public interface MasterFileUploadService {
	void processMasterFile(long parseLogId, boolean needRoster);
}
