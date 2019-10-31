package com.huajie.springboot.sample.spring.application.bootstrap;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.stream.Stream;

@ComponentScan(basePackageClasses = DefaultPackageBootstrap.class)
public class DefaultPackageBootstrap {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DefaultPackageBootstrap.class);
        Stream.of(context.getBeanDefinitionNames())
                .map(name -> "\t" + name)
                .forEach(System.out::println);
    }
}
