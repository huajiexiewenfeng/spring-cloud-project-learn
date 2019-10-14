package com.huajie.spring.cloud.client.controller;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringEventConntroller implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @RequestMapping("/send")
    public String send(@RequestParam String message){
        publisher.publishEvent(message);
        return "Sent";
    }

    @EventListener
    public void onMessage(PayloadApplicationEvent event){
        System.err.println("接收事件："+event.getPayload());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
