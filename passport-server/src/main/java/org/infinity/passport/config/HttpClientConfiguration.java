package org.infinity.passport.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.*;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@Configuration
public class HttpClientConfiguration {

    private final ApplicationProperties applicationProperties;

    @Autowired
    public HttpClientConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
        // SimpleClientHttpRequestFactory内部使用JDK的java.net.HttpURLConnection
        SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory
                .setReadTimeout(applicationProperties.getHttpClientConnection().getReadTimeoutInSeconds() * 1000);
        clientHttpRequestFactory.setConnectTimeout(1000);
        return clientHttpRequestFactory;
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpClient closeableHttpClient() {
        // HttpComponentsClientHttpRequestFactory内部使用Apache Http Client
        // PoolingHttpClientConnectionManager创建出的HttpClient实例就可以被多个连接及线程共用
        // 在应用容器起来的时候实例化一次，在整个应用结束的时候再调用httpClient.close()
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        // 整个连接池的并发
        connectionManager.setMaxTotal(40);
        // 每个主机的并发
        connectionManager.setDefaultMaxPerRoute(20);
        // 设置链接池和失败重试次数
        return HttpClientBuilder.create().setConnectionManager(connectionManager)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(
                        applicationProperties.getHttpClientConnection().getMaxRetries(), true))
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                closeableHttpClient());
        clientHttpRequestFactory
                .setReadTimeout(applicationProperties.getHttpClientConnection().getReadTimeoutInSeconds() * 1000);
        clientHttpRequestFactory.setConnectTimeout(1000);
        return clientHttpRequestFactory;
    }

    @Bean
    public RestTemplate globalRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.singletonList(new SecurityHeaderClientHttpRequestInterceptor()));
        return restTemplate;
    }

    static class SecurityHeaderClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
        @Override
        @NonNull
        public ClientHttpResponse intercept(@NonNull HttpRequest request, @NonNull byte[] body, ClientHttpRequestExecution execution)
                throws IOException {
            HttpRequestWrapper requestWrapper = new HttpRequestWrapper(request);
            // 服务端获取到该Header值，然后删除首8位,尾部4位，剩余部分转换为时间，如果改时间在误差范围之内为合法请求
            // millisecond: 13 bits
            requestWrapper.getHeaders().add("X-Security-Code", RandomStringUtils.randomNumeric(8)
                    + System.currentTimeMillis() + RandomStringUtils.randomNumeric(4));
            return execution.execute(requestWrapper, body);
        }
    }
}
