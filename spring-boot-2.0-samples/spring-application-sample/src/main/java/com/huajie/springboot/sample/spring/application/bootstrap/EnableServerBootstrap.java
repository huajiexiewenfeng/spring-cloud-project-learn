package com.huajie.springboot.sample.spring.application.bootstrap;

import com.huajie.springboot.sample.spring.application.annotation.EnableServer;
import com.huajie.springboot.sample.spring.application.server.Server;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableServer(type = Server.Type.FTP)
public class EnableServerBootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(EnableServerBootstrap.class);
        //启动上下文
        applicationContext.refresh();
        //获取容器中的bean对象
        Server server = applicationContext.getBean(Server.class);
        server.start();
        server.stop();
        applicationContext.close();
    }
}
