package com.huajie.spring.cloud.client.event;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RemoteAppEvent}事件监听器
 * 通过HTTP发送事件数据到目标服务器
 */
public class HttpRemoteAppEventListenter implements ApplicationListener<RemoteAppEvent> {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void onApplicationEvent(RemoteAppEvent event) {
        Object source = event.getSource();
        List<ServiceInstance> serviceInstances = event.getServiceInstances();
        for (ServiceInstance s : serviceInstances) {
            String rootURL = s.isSecure() ? "https://"+s.getHost()+":"+s.getPort() :
                    "http://"+s.getHost()+":"+s.getPort();
            String url = rootURL+"/receive/remote/event";
            Map<String,Object> data = new HashMap<>();
            data.put("sender",event.getSender());
            data.put("value",source);
            data.put("type",event.getType());

            String responseContext = restTemplate.postForObject(url, data, String.class);
        }
    }
}
