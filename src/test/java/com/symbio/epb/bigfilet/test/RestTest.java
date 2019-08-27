package com.symbio.epb.bigfilet.test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.symbio.epb.bigfile.Application;
import com.symbio.epb.bigfile.utils.JSONUtil;
@SpringBootTest(classes = Application.class)
@RunWith(SpringRunner.class)
public class RestTest {
	@Autowired
	private RestTemplate restTemplate;
    @Test 
    public void testLogin() throws URISyntaxException {
		URI url = new URI("http://localhost:8080/ajax_security_check");
		LinkedMultiValueMap<String, String> body=new LinkedMultiValueMap<String, String>();
        body.add("username","66776677");
        body.add("password","123456");
        body.add("site","dsSitelbsasaceBaguiocityph02");
        body.add("lob","sitelbsasace");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, String>> entity = new HttpEntity<LinkedMultiValueMap<String, String>>(body, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		System.out.println(exchange.toString());
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testUpload() throws Exception {
    	URI url = new URI("https://iqor.ppro360.com/ajax_security_check");
		LinkedMultiValueMap<String, Object> body=new LinkedMultiValueMap<String, Object>();
        body.add("username","66776677");
        body.add("password","123456");
        body.add("site","dsSitelbsasacebaguiocityph02");
        body.add("lob","iqorbambgim");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers);
		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		Map<String, Object> tet = (Map<String, Object>)JSONUtil.formatStr2Object(exchange.getBody(), Map.class);
		System.out.println("test---"+tet.get("JSESSIONID"));
		
//		URI url1 = new URI("http://localhost:8080/Admin/PerformanceUpload");
////		LinkedMultiValueMap body1=new LinkedMultiValueMap();
//		String fileLocal = "D:\\\\mnt\\\\mfsmount\\\\epeakbook\\\\IQORBAMBGIM\\\\liveDASMARINASPH02\\\\SITEDATA\\\\sitedata_iqorbambgim_dasmarinasph02_20180915.xlsx";
//		FileSystemResource resource = new FileSystemResource(new File(fileLocal ));
//		body.add("file", resource);
//		HttpHeaders headers1 = new HttpHeaders();
////		headers1.add("Cookie", tet.get("JSESSIONID").toString());
//		headers1.add("Content-Type", "multipart/form-data");
//		HttpEntity<LinkedMultiValueMap<String, Object>> entity1 = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers1);
//		ResponseEntity<String> en2 = restTemplate.exchange(url1, HttpMethod.POST, entity1, String.class);
//		System.out.println(en2);
	}
    @Test
    public void testUpload2() throws Exception {
    	
//    	URI url = new URI("http://localhost:8080/ajax_security_check");
//		LinkedMultiValueMap<String, Object> body=new LinkedMultiValueMap<String, Object>();
//        body.add("username","66776677");
//        body.add("password","123456");
//        body.add("site","dsSitelbsasaceBaguiocityph02");
//        body.add("lob","sitelbsasace");
//        
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//		HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers);
//		ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//		Map<String, Object> tet = (Map<String, Object>)JSONUtil.formatStr2Object(exchange.getBody(), Map.class);
    	 CredentialsProvider credsProvider = new BasicCredentialsProvider();
         credsProvider.setCredentials(
                 new AuthScope(null, -1),
                 new UsernamePasswordCredentials("66776677", "123456"));
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory);
		URI url1 = new URI("http://localhost:8080/Admin/PerformanceUpload");
//		LinkedMultiValueMap<String, Object> body1=new LinkedMultiValueMap<String, Object>();
		LinkedMultiValueMap<String, Object> body=new LinkedMultiValueMap<String, Object>();
		String fileLocal = "D:\\\\mnt\\\\mfsmount\\\\epeakbook\\\\SITELBSASACE\\\\BAGUIOCITYPH02\\\\ADJ\\\\adj_sitelbsasace_baguiocityph02_20180830.xlsx";
		FileSystemResource resource = new FileSystemResource(new File(fileLocal ));
		body.add("file", resource);
		HttpHeaders headers1 = new HttpHeaders();
        body.add("site","dsSitelbsasaceBaguiocityph02");
        body.add("lob","sitelbsasace");
		headers1.add("Content-Type", "multipart/form-data");
		HttpEntity<LinkedMultiValueMap<String, Object>> entity1 = new HttpEntity<LinkedMultiValueMap<String, Object>>(body, headers1);
		ResponseEntity<String> en2 = restTemplate.exchange(url1, HttpMethod.POST, entity1, String.class);
		System.out.println(en2);
    }
}
