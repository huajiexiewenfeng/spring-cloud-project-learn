package com.huajie.spring.cloud.servlet.gateway;

import com.huajie.spring.cloud.servlet.gateway.loadbalancer.ZookeeperLoadBalancer;
import com.netflix.loadbalancer.Server;
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
 * 集成ribbon负载均衡
 * /{service-name}/{service-uri}/->http://127.0.0.1:8080/hello-word
 */
@WebServlet(name = "proxy", urlPatterns = "/ribbon/gateway/*")
public class RibbonGatewayServlet extends HttpServlet {

    @Autowired
    private ZookeeperLoadBalancer zookeeperLoadBalancer;


    private Server chooseServer(String serviceName){
       return zookeeperLoadBalancer.chooseServer(serviceName);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String[] parts = StringUtils.split(pathInfo.substring(1), "/");
        String serviceName = parts[0];
        String uri = "/" + parts[1];
        Server server = chooseServer(serviceName);
        String targetUrl = builderTargetURL(server, uri, req);

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

    private String builderTargetURL(Server server, String serviceURI, HttpServletRequest req) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(server.getScheme())
                .append(server.getHost()).append(":")
                .append(server.getPort())
                .append(serviceURI);
        String queryString = req.getQueryString();
        if (StringUtils.hasText(queryString)) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

}
