package com.huajie.springboot.sample.spring.application.annotation;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.stream.Stream;

public class ServerImportDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        //复用之前的逻辑
        ImportSelector importSelector = new ServerImportSelector();
        //获取选择的ClassName
        String[] selectedClassNames = importSelector.selectImports(annotationMetadata);
        //创建bean定义
        Stream.of(selectedClassNames)
                //转化为BeanDefinitionBuilder对象
                .map(BeanDefinitionBuilder::genericBeanDefinition)
                //转化为BeanDefinition
                .map(BeanDefinitionBuilder::getBeanDefinition)
                .forEach(beanDefinition -> {
                    //注册到BeanDefinitionRegister
                    BeanDefinitionReaderUtils.registerWithGeneratedName(beanDefinition, beanDefinitionRegistry);
                });
    }
}
