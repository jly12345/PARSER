package com.symbio.epb.bigfile.service.impl;

import com.symbio.epb.bigfile.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsServiceImpl implements MyUserDetailsService {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
    private PasswordEncoder passwordEncoder;
	@Value("${epb.bigfile.uploader-username}")
	private String systemUsername;
	@Value("${epb.bigfile.uploader-password}")
	private String password;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	if (!username.equals(systemUsername)) {
    		String mString = "Username is invalid: "+ username;
 			logger.info(mString);
 			throw new UsernameNotFoundException(mString);
 		}
        String psd = passwordEncoder.encode(password);
        return new User(username, psd,  AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}