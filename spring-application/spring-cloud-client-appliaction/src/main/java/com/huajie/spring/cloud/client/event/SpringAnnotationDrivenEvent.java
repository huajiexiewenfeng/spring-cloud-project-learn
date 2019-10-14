package com.huajie.spring.cloud.client.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.EventListener;

import java.sql.SQLOutput;

public class SpringAnnotationDrivenEvent {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringAnnotationDrivenEvent.class);
        context.refresh();
        context.publishEvent(new MyApplicationEvent("hello word"));
        context.close();
    }

    private static class MyApplicationEvent extends ApplicationEvent{

        /**
         * Create a new ApplicationEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public MyApplicationEvent(Object source) {
            super(source);
        }

    }

    @EventListener
    public void OnMessage(MyApplicationEvent e){
        System.err.printf("[%s]事件2：%s\n", Thread.currentThread().getName(), e);
    }
}
