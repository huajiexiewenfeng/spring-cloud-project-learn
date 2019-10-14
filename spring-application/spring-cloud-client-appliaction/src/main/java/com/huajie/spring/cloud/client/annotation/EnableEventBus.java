package com.huajie.spring.cloud.client.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({RestClientsRegistrar.class})
public @interface EnableEventBus {

    /**
     * 指定@RestClient接口
     * @return
     */
    Class<?>[] clients() default {};
}
