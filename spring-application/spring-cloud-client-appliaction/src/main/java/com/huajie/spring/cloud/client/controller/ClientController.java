package com.huajie.spring.cloud.client.controller;

import com.huajie.spring.cloud.client.annotation.CustomedLoadBalance;
import com.huajie.spring.cloud.client.loadbalance.LoadBalancedRequestInterceptor;
import com.huajie.spring.cloud.client.service.rest.clients.SayingRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class ClientController {

    @Autowired
    @CustomedLoadBalance
    private RestTemplate restTemplate;

    @Autowired
    @LoadBalanced
    private RestTemplate lbRestTemplate;

    @Autowired
    private SayingRestService sayingRestService;

    @GetMapping("/rest/say")
    public Object restSay(@RequestParam("message") String message) {
        return sayingRestService.say(message);
    }
//
//    @Autowired
//    private DiscoveryClient discoveryClient;

//    @Value("${spring.server.name}")
//    private String currentServiceName;

    //线程安全
//    private volatile Set<String> targetUrls = new HashSet<>();
//    private volatile Map<String, Set<String>> targetUrlsCache = new HashMap<>();
//
//    @Scheduled(fixedRate = 10 * 1000)//10秒更新一次缓存
//    public void updateTargetUrlsCache() {
//        Map<String, Set<String>> newTargetUrlsCache = new HashMap<>();
//        discoveryClient.getServices().forEach(serviceName -> {
//            //获取当前应用的机器列表
//            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
//            Set<String> newTargetUrls = serviceInstances.stream().map(
//                    s -> s.isSecure() ?
//                            "https://" + s.getHost() + ":" + s.getPort()
//                            : "http://" + s.getHost() + ":" + s.getPort()
//            ).collect(Collectors.toSet());
//            newTargetUrlsCache.put(serviceName, newTargetUrls);
//        });
//        this.targetUrlsCache = newTargetUrlsCache;
//    }

//    @Scheduled(fixedRate = 10 * 1000)//10秒更新一次缓存
//    public void updateTargetUrls() {
//        Set<String> olaTargetUrls = this.targetUrls;
//        //获取当前应用的机器列表
//        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(currentServiceName);
//        Set<String> newTargetUrls = serviceInstances.stream().map(
//                s -> s.isSecure() ?
//                        "https://" + s.getHost() + ":" + s.getPort()
//                        : "http://" + s.getHost() + ":" + s.getPort()
//        ).collect(Collectors.toSet());
//        //swag
//        this.targetUrls = newTargetUrls;
//        olaTargetUrls.clear();
//    }

    @GetMapping("/invoke/{serviceName}/say")
    public Object invokeSay(@RequestParam("message") String message, @PathVariable("serviceName") String serviceName) {
//        //服务器列表
//        List<String> targetUrls = new ArrayList<>(this.targetUrlsCache.get(serviceName));
//        //轮训列表
//        //选择其中一台服务器
//        int size = targetUrls.size();
//        int index = new Random().nextInt(size);
//        String targetUrl = targetUrls.get(index);
        //RestTemplate发送请求到服务器
        return restTemplate.getForObject("/" + serviceName + "/say?message=" + message, String.class);
        //输出响应
    }

    @GetMapping("/loadbalance/invoke/{serviceName}/say")
    public Object lbInvokeSay(@RequestParam("message") String message, @PathVariable("serviceName") String serviceName) {
        return lbRestTemplate.getForObject("http://" + serviceName + "/say?message=" + message, String.class);
        //输出响应
    }
//    @GetMapping("/invoke/say")
//    public Object invokeSay(@RequestParam("message") String message) {
//        //服务器列表
//        List<String> targetUrls = new ArrayList<>(this.targetUrls);
//        //轮训列表
//        //选择其中一台服务器
//        int size = targetUrls.size();
//        int index = new Random().nextInt(size);
//        String targetUrl = targetUrls.get(index);
//        //RestTemplate发送请求到服务器
//        return restTemplate.getForObject(targetUrl + "/say?message=" + message, String.class);
//        //输出响应
//    }

    @Bean
    public ClientHttpRequestInterceptor interceptor() {
        return new LoadBalancedRequestInterceptor();
    }

    @Bean
    @LoadBalanced
    public RestTemplate loadBalanceRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @CustomedLoadBalance
    public RestTemplate customLoadBalanceRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    @Autowired
    public Object customizer(@CustomedLoadBalance Collection<RestTemplate> restTemplates, ClientHttpRequestInterceptor interceptor) {
        restTemplates.forEach(restTemplate -> {
                    //增加拦截器
                    restTemplate.setInterceptors(Arrays.asList(interceptor));
                }
        );
        return new Object();
    }

}
