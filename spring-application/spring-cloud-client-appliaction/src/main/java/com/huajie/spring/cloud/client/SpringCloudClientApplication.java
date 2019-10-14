package com.huajie.spring.cloud.client;

import com.huajie.spring.cloud.client.annotation.EnableRestClients;
import com.huajie.spring.cloud.client.event.HttpRemoteAppEventListenter;
import com.huajie.spring.cloud.client.service.rest.clients.SayingRestService;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
@EnableRestClients(clients = {SayingRestService.class})
public class SpringCloudClientApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringCloudClientApplication.class)
                .web(WebApplicationType.SERVLET)
                .listeners(new HttpRemoteAppEventListenter())
                .run(args);
    }


}
