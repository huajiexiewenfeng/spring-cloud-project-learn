package com.huajie.spring.cloud.config.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@EnableScheduling
@SpringBootApplication
public class SpringCloudConfigClient {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClient.class, args);
    }




}

