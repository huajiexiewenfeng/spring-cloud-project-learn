package com.huajie.spring.cloud.client.event;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RemoteAppEvent}事件监听器
 * 通过HTTP发送事件数据到目标服务器
 * 通过SmartApplicationListener监听多个事件
 */
public class HttpRemoteAppEventListenter implements SmartApplicationListener {

    private RestTemplate restTemplate = new RestTemplate();

    private DiscoveryClient discoveryClient;

    private String currentAppName;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return RemoteAppEvent.class.isAssignableFrom(eventType) ||
                ContextRefreshedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof RemoteAppEvent) {
            RemoteAppEvent appEvent = (RemoteAppEvent) event;
            Object source = event.getSource();
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(appEvent.getAppName());
            for (ServiceInstance s : serviceInstances) {
                String rootURL = s.isSecure() ? "https://" + s.getHost() + ":" + s.getPort() :
                        "http://" + s.getHost() + ":" + s.getPort();
                String url = rootURL + "/receive/remote/event";
                Map<String, Object> data = new HashMap<>();
                data.put("value", source);
                data.put("type", appEvent.getClass().getName());
                data.put("sender",currentAppName);
                String responseContext = restTemplate.postForObject(url, data, String.class);
            }
        } else if (event instanceof ContextRefreshedEvent) {
            ContextRefreshedEvent refreshedEvent = (ContextRefreshedEvent) event;
            ApplicationContext applicationContext = refreshedEvent.getApplicationContext();
            this.discoveryClient = applicationContext.getBean(DiscoveryClient.class);
            this.currentAppName = applicationContext.getEnvironment().getProperty("spring.application.name");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
