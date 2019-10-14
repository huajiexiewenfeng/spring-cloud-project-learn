package com.huajie.springboot.sample.spring.application.server;

public interface Server {
    //启动服务器
    void start();
    //关闭服务器
    void stop();

    enum Type{
        HTTP,
        FTP
    }
}
