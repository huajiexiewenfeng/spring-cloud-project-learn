package com.huajie.spring.cloud.server.event;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class RemoteAppEvent extends ApplicationEvent {

    /**
     * http rpc mq
     */
    private String type;

    private String appName;

    private String sender;

    private List<ServiceInstance> serviceInstances;

    public RemoteAppEvent(Object source, String type, String appName, String sender, List<ServiceInstance> serviceInstances) {
        super(source);
        this.type = type;
        this.appName = appName;
        this.sender = sender;
        this.serviceInstances = serviceInstances;
    }

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public RemoteAppEvent(Object source, String appName, String sender, List<ServiceInstance> serviceInstances) {
        super(source);

    }

    public String getType() {
        return type;
    }

    public String getAppName() {
        return appName;
    }

    public List<ServiceInstance> getServiceInstances() {
        return serviceInstances;
    }

    public String getSender() {
        return sender;
    }
}
