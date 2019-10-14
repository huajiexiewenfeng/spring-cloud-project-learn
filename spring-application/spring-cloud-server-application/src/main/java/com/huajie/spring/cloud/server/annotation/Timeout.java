package com.huajie.spring.cloud.server.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Timeout {
    int timeout() default 100;
    String fallback() default "";
}
