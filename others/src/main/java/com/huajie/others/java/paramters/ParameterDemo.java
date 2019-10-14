package com.huajie.others.java.paramters;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * 测试反射获取类方法参数
 */
public class ParameterDemo {
    public String say(String message){
        return "hello world:"+message;
    }

    public static void main(String[] args) {
        Method method = ReflectionUtils.findMethod(ParameterDemo.class,"say",String.class);
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        Stream.of(discoverer.getParameterNames(method)).forEach(System.out::println);
        methodTest();
    }

    public static void methodTest() {
        Method method = ReflectionUtils.findMethod(ParameterDemo.class,"say",String.class);
        Parameter parameter = method.getParameters()[0];
        System.out.println(parameter.getName());
    }
}
