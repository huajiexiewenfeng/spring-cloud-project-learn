package com.huajie.spring.cloud.server.controller;

import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Payload;
import java.util.Map;

@RestController
public class RemoteAppEventReceiverController implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @PostMapping("/receive/remote/event")
    public String receive(@RequestBody Map<String, Object> data) {
        //事件的发送者
        String sender = (String) data.get("sender");
        //时间的数据内容
        Object value = data.get("value");
        //时间类型
        String type = (String) data.get("type");

        publisher.publishEvent(new SenderRemoteAppEvent(value,sender));

        return "received";

    }

    private class SenderRemoteAppEvent extends ApplicationEvent{

        private String sender;

        /**
         * Create a new ApplicationEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public SenderRemoteAppEvent(Object source,String sender) {
            super(source);
            this.sender =sender;
        }

        public String getSender() {
            return sender;
        }
    }

    @EventListener
    private void onEvent(SenderRemoteAppEvent data){
        System.out.println(data.getSender()+":"+data.getSource());
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
