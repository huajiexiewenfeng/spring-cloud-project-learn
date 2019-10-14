package com.huajie.spring.cloud.servlet.gateway;

import com.huajie.spring.cloud.servlet.gateway.loadbalancer.ZookeeperLoadBalancer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@ServletComponentScan(basePackages = "com.huajie.spring.cloud.servlet.gateway")
public class SpringCloudServletGatewayApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudServletGatewayApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
//        SpringApplication.run(SpringCloudServletGatewayApplication.class, args);
    }

    @Bean
    public ZookeeperLoadBalancer zookeeperLoadBalancer(DiscoveryClient discoveryClient){
        return new ZookeeperLoadBalancer(discoveryClient);
    }
}
