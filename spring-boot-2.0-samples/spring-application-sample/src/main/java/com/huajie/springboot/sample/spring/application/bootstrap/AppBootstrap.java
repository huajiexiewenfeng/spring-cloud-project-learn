package com.huajie.springboot.sample.spring.application.bootstrap;

import com.huajie.springboot.sample.spring.application.autoconfig.WebAutoConfiguration;
import com.huajie.springboot.sample.spring.application.config.WebConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = "com.huajie.springboot.sample.spring.application.config")
@EnableAutoConfiguration
public class AppBootstrap {
    public static void main(String[] args) {
//        SpringApplication.run(AppBootstrap.class, args);
        SpringApplication.run(AppBootstrap.class, args);
    }

}
