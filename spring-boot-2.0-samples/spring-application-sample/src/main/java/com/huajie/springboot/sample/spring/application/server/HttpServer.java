package com.huajie.springboot.sample.spring.application.server;

import org.springframework.stereotype.Component;

@Component
public class HttpServer implements Server {
    @Override
    public void start() {
        System.out.println("Http服务器启动");
    }

    @Override
    public void stop() {
        System.out.println("Http服务器关闭");
    }
}
