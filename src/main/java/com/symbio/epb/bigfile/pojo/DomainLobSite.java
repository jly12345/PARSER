package com.symbio.epb.bigfile.pojo;

import java.io.Serializable;


/**
 * 
 * @author pan.yao
 *
 */
//@Entity
public class DomainLobSite implements Serializable{
	
	private static final long serialVersionUID = -5192399290578788617L;
	
//	@Id
//	@GeneratedValue
	private long id;
    private String domain;
    private String lobSite;
    private String site;
    private String lob;
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLobSite() {
        return lobSite;
    }

    public void setLobSite(String lobSite) {
        this.lobSite = lobSite;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}
    
}
