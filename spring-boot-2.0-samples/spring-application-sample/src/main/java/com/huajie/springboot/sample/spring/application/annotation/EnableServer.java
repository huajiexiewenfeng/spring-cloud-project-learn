package com.huajie.springboot.sample.spring.application.annotation;

import com.huajie.springboot.sample.spring.application.config.HelloWorldConfiguration;
import com.huajie.springboot.sample.spring.application.server.Server;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
//@Import(ServerImportSelector.class)
@Import(ServerImportDefinitionRegistrar.class)
public @interface EnableServer {

    //设置服务器类型
    Server.Type type() default Server.Type.HTTP;

}
