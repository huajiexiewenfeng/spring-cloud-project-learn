package com.huajie.spring.cloud.client.controller;

import com.huajie.spring.cloud.client.event.RemoteAppEvent;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.*;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 远程事件控制器
 */
@RestController
public class RemoteSpringEventSenderConntroller implements ApplicationEventPublisherAware {

//    @Autowired
//    private DiscoveryClient discoveryClient;

    private ApplicationEventPublisher publisher;

    @RequestMapping("/send/remote/event")
    public String send(@RequestParam String message) {
        publisher.publishEvent(message);
        return "Sent";
    }

//    @RequestMapping("/send/remote/event/{appName}")
//    public String sendAppCluster(@PathVariable String appName, @RequestBody Map data) {
//        //发送数据到自己
//        List<ServiceInstance> services = discoveryClient.getInstances(appName);
//        RemoteAppEvent remoteAppEvent = new RemoteAppEvent(data, "http", appName, currentAppName, services);
//        //发送事件到当前的上下文
//        publisher.publishEvent(remoteAppEvent);
//        return "Ok";
//    }
//http://127.0.0.1:8082//send/remote/event/spring-application-server?message=helloword11
    @RequestMapping("/send/remote/event/{appName}")
    public String sendAppCluster(@PathVariable String appName, @RequestBody Map data) {
        //发送数据到自己
        RemoteAppEvent remoteAppEvent = new RemoteAppEvent(data, appName, true);
        //发送事件到当前的上下文
        publisher.publishEvent(remoteAppEvent);
        return "Ok";
    }

//    @RequestMapping("/send/remote/event/{appName}/{ip}/{port}")
//    public String sendAppInstance(@PathVariable String appName,
//                                  @PathVariable String ip,
//                                  @PathVariable int port
//            , @RequestBody Map data) {
//        //发送数据到自己
////        List<ServiceInstance> services = discoveryClient.getInstances(appName);
////        List<ServiceInstance> collect = services.stream().filter(serviceInstance ->
////                serviceInstance.getHost().equals(ip) && serviceInstance.getPort() == port
////        ).collect(Collectors.toList());
//        ServiceInstance serviceInstance = new DefaultServiceInstance(appName, ip, port, false);
//        RemoteAppEvent remoteAppEvent = new RemoteAppEvent(data, "http", appName, currentAppName, Arrays.asList(serviceInstance));
//        //发送事件到当前的上下文
//        publisher.publishEvent(remoteAppEvent);
//        return "Ok";
//    }


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}
