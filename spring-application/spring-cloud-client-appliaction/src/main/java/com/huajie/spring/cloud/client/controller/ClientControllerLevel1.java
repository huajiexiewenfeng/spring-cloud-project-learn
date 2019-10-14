//package com.huajie.spring.cloud.client.controller;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//public class ClientControllerLevel1 {
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private DiscoveryClient discoveryClient;
//
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
//
//    @GetMapping("/invoke/{serviceName}/say")
//    public Object invokeSay(@RequestParam("message") String message, @PathVariable("serviceName") String serviceName) {
//        //服务器列表
//        List<String> targetUrls = new ArrayList<>(this.targetUrlsCache.get(serviceName));
//        //轮训列表
//        //选择其中一台服务器
//        int size = targetUrls.size();
//        int index = new Random().nextInt(size);
//        String targetUrl = targetUrls.get(index);
//        //RestTemplate发送请求到服务器
//        return restTemplate.getForObject(targetUrl + "/say?message=" + message, String.class);
//    }
//
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//
//
//}
