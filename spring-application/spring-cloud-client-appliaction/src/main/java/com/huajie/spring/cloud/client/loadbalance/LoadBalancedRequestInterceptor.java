package com.huajie.spring.cloud.client.loadbalance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

public class LoadBalancedRequestInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private DiscoveryClient discoveryClient;

    private volatile Map<String, Set<String>> targetUrlsCache = new HashMap<>();

    @Scheduled(fixedRate = 10 * 1000)//10秒更新一次缓存
    public void updateTargetUrlsCache() {
        Map<String, Set<String>> newTargetUrlsCache = new HashMap<>();
        discoveryClient.getServices().forEach(serviceName -> {
            //获取当前应用的机器列表
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
            Set<String> newTargetUrls = serviceInstances.stream().map(
                    s -> s.isSecure() ?
                            "https://" + s.getHost() + ":" + s.getPort()
                            : "http://" + s.getHost() + ":" + s.getPort()
            ).collect(Collectors.toSet());
            newTargetUrlsCache.put(serviceName, newTargetUrls);
        });
        this.targetUrlsCache = newTargetUrlsCache;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        //URI:/${app-name}/${uri}
        URI requestUri = httpRequest.getURI();
        String path = requestUri.getPath();
        String[] parts = path.split("/");
        if ("/".equals(path.substring(1, 1))) {
            parts = path.substring(1).split("/");
        }
        String appName = parts[1];
        String uri = parts[2];
        //服务器列表
        List<String> targetUrls = new ArrayList<>(this.targetUrlsCache.get(appName));
        //轮训列表
        //选择其中一台服务器
        int size = targetUrls.size();
        int index = new Random().nextInt(size);
        String targetUrl = targetUrls.get(index);
        //最终服务器地址
        String actualUrl = targetUrl + "/" + uri + "?" + requestUri.getQuery();
        //默认用jackson转会报错
        List<HttpMessageConverter<?>> messageConverters = Arrays.asList(
                new ByteArrayHttpMessageConverter(),
                new StringHttpMessageConverter());

//        RestTemplate restTemplate = new RestTemplate(messageConverters);
//        ResponseEntity<InputStream> entity = restTemplate.getForEntity(actualUrl, InputStream.class);
//        InputStream body = entity.getBody();
//        HttpHeaders headers = entity.getHeaders();
        URL url = new URL(actualUrl);
        URLConnection urlConnection = url.openConnection();
        InputStream body = urlConnection.getInputStream();
        HttpHeaders headers = new HttpHeaders();
        return new SimpleClientHttpResponse(headers,body);
    }

    class SimpleClientHttpResponse implements ClientHttpResponse{

        private HttpHeaders httpHeaders;
        private InputStream body;

        public SimpleClientHttpResponse(HttpHeaders httpHeaders, InputStream body) {
            this.httpHeaders = httpHeaders;
            this.body = body;
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return HttpStatus.OK;
        }

        @Override
        public int getRawStatusCode() throws IOException {
            return 200;
        }

        @Override
        public String getStatusText() throws IOException {
            return "OK";
        }

        @Override
        public void close() {

        }

        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return httpHeaders;
        }
    }

}
