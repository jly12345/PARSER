package com.symbio.epb.bigfile.mapper;

import com.symbio.epb.bigfile.pojo.DomainLobSite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 
 * @author Yao Pan
 *
 */
@Mapper
public interface DomainLobSiteMapper {
	@Select("SELECT * FROM domain_lob_site")
	List<DomainLobSite> findAll();
	@Select("SELECT lob_site FROM domain_lob_site")
	List<String> findAllLobSite();
}
