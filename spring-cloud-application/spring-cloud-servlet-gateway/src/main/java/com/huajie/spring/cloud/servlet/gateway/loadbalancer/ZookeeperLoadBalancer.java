package com.huajie.spring.cloud.servlet.gateway.loadbalancer;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ZookeeperLoadBalancer extends BaseLoadBalancer {

    private String applicationName = "spring-servlet-gateway";

    private DiscoveryClient discoveryClient;

    public ZookeeperLoadBalancer(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        updateServers();
    }

    /**
     * 更新所有服务器
     */
    @Scheduled(fixedRate = 10 * 1000)//10秒更新一次缓存
    private void updateServers() {
        discoveryClient.getServices().stream().filter(s -> !applicationName.equals(s)).forEach(serviceName -> {
            List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
            serviceInstances.forEach(serviceInstance -> {
                Server server = new Server(serviceInstance.isSecure()?"https://":"http://",
                        serviceInstance.getHost(),serviceInstance.getPort());
                addServer(server);
            });
        });

    }

}
