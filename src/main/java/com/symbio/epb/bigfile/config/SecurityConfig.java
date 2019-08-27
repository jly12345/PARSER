package com.symbio.epb.bigfile.config;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.symbio.epb.bigfile.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  //  启用方法级别的权限认证
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	http.formLogin()                    //  定义当需要用户登录时候，转到的登录页面。
        .loginPage("/login")           // 设置登录页面
        .loginProcessingUrl("/autoupload")  // 自定义的登录接口
        .failureHandler(new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.setContentType("application/json;charset=utf-8");
                 PrintWriter out = response.getWriter();
                 StringBuffer sb = new StringBuffer();
                 sb.append("{\"status\":\"error\",\"msg\":\"");
                 if (exception instanceof UsernameNotFoundException || exception instanceof BadCredentialsException) {
                     sb.append("Invalid HR ID or password!");
                 }  else {
                     sb.append("Login Failed!");
                 }
                 sb.append("\"}");
                 out.write(sb.toString());
                 out.flush();
                 out.close();

			}
        	
        })
        .and()
        .authorizeRequests()        // 定义哪些URL需要被保护、哪些不需要被保护
        .antMatchers("/login","/**/*.css","/**/*.js",
        		"/**/bigfile","/**/sitefile", //之前不支持自动上传的相关URL
        		"/**/bigfile/upload",
        		"/**/bigfile/download",
        		"/**/sitefile/upload",
        		"/**/sitefile/download",
        		"/**/getDomainLobSiteMapping").permitAll()     // 设置所有人都可以访问登录页面
        .anyRequest()               // 任何请求,登录后可以访问
        .authenticated()
        .and()
        .logout().logoutSuccessUrl("/login.html")
        .invalidateHttpSession(true) 
        .and()
        .csrf().disable();          // 关闭csrf防护
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
         return new BCryptPasswordEncoder();
     }


}
