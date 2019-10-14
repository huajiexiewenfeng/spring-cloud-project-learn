package com.huajie.springboot.sample.spring.application.annotation;

import com.huajie.springboot.sample.spring.application.config.HelloWorldConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HelloWorldConfiguration.class)
public @interface EnableHelloWorld {
}
