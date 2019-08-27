package com.symbio.epb.bigfile.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.symbio.epb.bigfile.pojo.BigFileParseLog;

/**
 * 
 * @author Yao Pan
 *
 */
@Mapper
public interface ParseLogMapper {
	@Insert("INSERT INTO big_file_parse_log "
			+ "(file_name,file_path,file_type,file_date,start_time,end_time,update_time,status)"
			+ " VALUES "
			+ "(#{fileName},#{filePath},#{fileType},#{fileDate},#{startTime},#{endTime},#{updateTime},#{status})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	int insert(BigFileParseLog bigFileParseLog);
	
	@Update("UPDATE big_file_parse_log SET status=#{status}, end_time=#{endTime},update_time=#{updateTime} WHERE id=#{id}")
	int updateStatusAndEndTimeById(BigFileParseLog bigFileParseLog);
	
	@Update("UPDATE big_file_parse_log SET status=#{status},update_time=#{updateTime} WHERE id=#{id}")
	int updateStatusById(BigFileParseLog bigFileParseLog);
	
	@Select("SELECT * FROM big_file_parse_log WHERE file_type=#{fileType} AND status != 2")
	List<BigFileParseLog> findParseLogByType(int fileType);
	
	@Select("SELECT * FROM big_file_parse_log WHERE file_date =#{fileDate} AND file_type=#{fileType} and status != 2")
	List<BigFileParseLog> findParseLogByFileDate(@Param(value = "fileDate")Timestamp fileDate,@Param(value = "fileType")int fileType);
}
