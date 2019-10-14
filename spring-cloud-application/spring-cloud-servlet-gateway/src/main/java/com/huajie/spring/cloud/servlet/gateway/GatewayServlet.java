package com.huajie.spring.cloud.servlet.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.print.URIException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 服务网关的路由规则
 * /{service-name}/{service-uri}/->http://127.0.0.1:8080/hello-word
 */
@WebServlet(name = "proxy", urlPatterns = "/gateway/*")
public class GatewayServlet extends HttpServlet {

    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 随机算法
     *
     * @param serviceName
     * @return
     */
    private ServiceInstance randomChoose(String serviceName) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        int size = instances.size();
        Random random = new Random();
        int index = random.nextInt(size);
        return instances.get(index);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] parts = StringUtils.split(pathInfo.substring(1), "/");
        String serviceName = parts[0];
        String uri = "/" + parts[1];
        ServiceInstance serviceInstance = randomChoose(serviceName);
        String targetUrl = builderTargetURL(serviceInstance, uri, req);

        RestTemplate restTemplate = new RestTemplate();
        RequestEntity<byte[]> requestEntity = null;
        try {
            requestEntity = createRequestEntity(req, targetUrl);
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(requestEntity, byte[].class);
            writeHeaders(responseEntity, resp);
            writeBody(responseEntity, resp);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出 Body 部分
     *
     * @param responseEntity
     * @param response
     * @throws IOException
     */
    private void writeBody(ResponseEntity<byte[]> responseEntity, HttpServletResponse response) throws IOException {
        if (responseEntity.hasBody()) {
            byte[] body = responseEntity.getBody();
            // 输出二进值
            ServletOutputStream outputStream = response.getOutputStream();
            // 输出 ServletOutputStream
            outputStream.write(body);
            outputStream.flush();
        }
    }

    private void writeHeaders(ResponseEntity<byte[]> responseEntity, HttpServletResponse response) {
        // 获取相应头
        HttpHeaders httpHeaders = responseEntity.getHeaders();
        // 输出转发 Response 头
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            String headerName = entry.getKey();
            List<String> headerValues = entry.getValue();
            for (String headerValue : headerValues) {
                response.addHeader(headerName, headerValue);
            }
        }
    }

    private RequestEntity<byte[]> createRequestEntity(HttpServletRequest req, String targetUrl) throws IOException, URISyntaxException {
        String method = req.getMethod();
        HttpMethod httpMethod = HttpMethod.resolve(method);
        byte[] body = createRequestBody(req);
        MultiValueMap<String, String> headers = createRequestHeaders(req);
        RequestEntity<byte[]> requestEntity = new RequestEntity<>(body, headers, httpMethod, new URI(targetUrl));
        return requestEntity;
    }

    private MultiValueMap<String, String> createRequestHeaders(HttpServletRequest req) {
        HttpHeaders headers = new HttpHeaders();
        ArrayList<String> headersNames = Collections.list(req.getHeaderNames());
        for (String headersName : headersNames) {
            List<String> headerValues =  Collections.list(req.getHeaders(headersName));
            for (String headerValue : headerValues) {
                headers.add(headersName, headerValue);
            }
        }

        return headers;
    }

    private byte[] createRequestBody(HttpServletRequest req) throws IOException {
        ServletInputStream inputStream = req.getInputStream();
        return StreamUtils.copyToByteArray(inputStream);
    }

    private String builderTargetURL(ServiceInstance serviceInstance, String serviceURI, HttpServletRequest req) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(serviceInstance.isSecure() ? "https://" : "http://")
                .append(serviceInstance.getHost()).append(":")
                .append(serviceInstance.getPort())
                .append(serviceURI);
        String queryString = req.getQueryString();
        if (StringUtils.hasText(queryString)) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

}
