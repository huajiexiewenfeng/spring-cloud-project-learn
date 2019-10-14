package com.huajie.springboot.sample.spring.application.bootstrap;

import com.huajie.springboot.sample.spring.application.annotation.EnableHelloWorld;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

@EnableHelloWorld
@Configuration
public class EnableHelloWorldBootstrap {
    public static void main(String[] args) {
//        SpringApplication.run(EnableHelloWordBootstrap.class, args);
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(EnableHelloWorldBootstrap.class);
        //启动上下文
        applicationContext.refresh();
        //获取容器中的bean对象
        String helloWorld = applicationContext.getBean("helloWorld", String.class);
        System.out.println(helloWorld);
        applicationContext.close();

    }

}
