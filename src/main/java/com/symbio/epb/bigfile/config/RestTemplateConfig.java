package com.symbio.epb.bigfile.config;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
/**
 * 
 * @author Yao Pan
 *
 */
@Configuration
@ConditionalOnClass(value = {RestTemplate.class, CloseableHttpClient.class})
public class RestTemplateConfig {


    // restTemplate的 java配置开始
    private int maxTotalConnection = 500; //连接池的最大连接数

    private int maxConnectionPerRoute = 1000; //同路由的并发数

    private int connectionTimeout = 2 * 1000; //连接超时，默认2s

    private int readTimeout = 30 * 1000; //读取超时，默认30s


    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public void setMaxConnectionPerRoute(int maxConnectionPerRoute) {
        this.maxConnectionPerRoute = maxConnectionPerRoute;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }


    //创建HTTP客户端工厂
    @Bean(name = "clientHttpRequestFactory")
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        return createClientHttpRequestFactory(this.connectionTimeout, this.readTimeout);
    }

    //初始化RestTemplate,并加入spring的Bean工厂，由spring统一管理
    @Bean(name = "restTemplate")
    @Primary
    @Scope("prototype")
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return createRestTemplate(factory);
    }



    private ClientHttpRequestFactory createClientHttpRequestFactory(int connectionTimeout, int readTimeout) {
        //maxTotalConnection 和 maxConnectionPerRoute 必须要配
        if (this.maxTotalConnection <= 0) {
            throw new IllegalArgumentException("invalid maxTotalConnection: " + maxTotalConnection);
        }
        if (this.maxConnectionPerRoute <= 0) {
            throw new IllegalArgumentException("invalid maxConnectionPerRoute: " + maxTotalConnection);
        }

        //全局默认的header头配置
        List<Header> headers = new LinkedList<>();

        //创建真正处理http请求的httpClient实例
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultHeaders(headers)
                .build();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                httpClient);
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
        RestTemplate restTemplate = new RestTemplate(factory);

        //设置错误处理器
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        return restTemplate;
    }

}
