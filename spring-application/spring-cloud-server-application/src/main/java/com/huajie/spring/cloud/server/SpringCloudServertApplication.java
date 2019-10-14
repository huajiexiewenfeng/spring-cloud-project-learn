package com.huajie.spring.cloud.server;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableCircuitBreaker
public class SpringCloudServertApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudServertApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

}
