package com.huajie.spring.cloud.client.event;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class RemoteAppEvent extends ApplicationEvent {

    private final String appName;

    private final boolean isCluster;

    public RemoteAppEvent(Object source, String appName, boolean isCluster) {
        super(source);
        this.appName = appName;
        this.isCluster = isCluster;
    }

    public String getAppName() {
        return appName;
    }

    public boolean isCluster() {
        return isCluster;
    }

}
