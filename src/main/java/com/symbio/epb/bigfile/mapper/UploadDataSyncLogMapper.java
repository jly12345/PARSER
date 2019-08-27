package com.symbio.epb.bigfile.mapper;
/**
 * 
 * @author Yao Pan
 *
 */

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.symbio.epb.bigfile.pojo.UploadDataSyncLog;
/**
 * 
 * @author Yao Pan
 *
 */
@Mapper
public interface UploadDataSyncLogMapper {
	@Insert("INSERT INTO upload_data_sync_log "
	+ " (lob_name,domain_name,site_name,file_name,file_path,file_date,type,create_time,update_time,uploader, status, attempt_number,comment,parse_log_id) "
	+ " VALUES "
	+ " (#{lobName},#{domainName},#{siteName},#{fileName},#{filePath},#{fileDate},#{type},#{createTime},#{updateTime},#{uploader},#{status},#{attemptNumber},#{comment},#{parseLogId})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insert(UploadDataSyncLog dataSyncLog);
	
	@Update("UPDATE upload_data_sync_log "
	+ " SET "
	+ " update_time=#{updateTime},status=#{status},attempt_number=#{attemptNumber},comment=#{comment} "
	+ " WHERE id=#{id}")
	int updateStatusById(UploadDataSyncLog dataSyncLog);
	
	@Select("SELECT lob_name,domain_name,site_name,file_name,file_date,type,create_time,update_time,uploader, status, attempt_number,comment"
			+ " FROM upload_data_sync_log where parse_log_id=#{parseLogId}")
	List<UploadDataSyncLog> findUploadDataSyncLogByParseLogId(long parseLogId);
	
	@Select("SELECT s.id,s.lob_name,s.domain_name,s.site_name,s.file_name,s.file_path,s.file_date, "
			+ " s.type,s.create_time,s.update_time,s.uploader,s.status,s.attempt_number,s.comment "
			+ " FROM upload_data_sync_log s "
			+ " LEFT JOIN big_file_parse_log l ON s.parse_log_id = l.id "
			+ " WHERE s.parse_log_id=#{parseLogId} AND (s.status=0 OR s.status=2) "
			+ " AND l.status != 3")
	List<UploadDataSyncLog> findUnsuccessSyncLogByParseLogId(long parseLogId);
}
