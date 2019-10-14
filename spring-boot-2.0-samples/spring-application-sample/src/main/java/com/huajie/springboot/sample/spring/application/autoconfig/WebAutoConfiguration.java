package com.huajie.springboot.sample.spring.application.autoconfig;

import com.huajie.springboot.sample.spring.application.config.WebConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Web 自动装配类
 */
@Configuration
@Import(WebConfiguration.class)
public class WebAutoConfiguration {
}