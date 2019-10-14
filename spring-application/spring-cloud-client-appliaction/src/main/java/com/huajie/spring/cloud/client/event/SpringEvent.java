package com.huajie.spring.cloud.client.event;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

public class SpringEvent {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

//        //增加ContextRefreshedEvent监听
//        context.addApplicationListener((ApplicationListener<ContextRefreshedEvent>) e -> {
//            System.err.printf("[%s]事件：%s\n", Thread.currentThread().getName(), e);
//        });

        context.addApplicationListener((ApplicationListener<?>) e -> {
            System.err.printf("[%s]事件2：%s\n", Thread.currentThread().getName(), e);
        });


        context.refresh();
        context.close();
    }
}
