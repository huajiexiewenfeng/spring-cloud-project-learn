package com.huajie.springboot.sample.spring.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloWorldConfiguration {

    @Bean
    public String helloWorld(){
        return "hello world";
    }
}
