package com.huajie.others.java.paramters;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

/**
 * 测试反射获取接口方法参数
 */
public interface ParameterInterfaceDemo {
    String say(String message);

    public static void main(String[] args) {
        Method method = ReflectionUtils.findMethod(ParameterInterfaceDemo.class,"say",String.class);
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        try {
            Stream.of(discoverer.getParameterNames(method)).forEach(System.out::println);
        }catch (Exception e){
            e.printStackTrace();
        }
        methodTest();
    }

    public static void methodTest() {
        Method method = ReflectionUtils.findMethod(ParameterInterfaceDemo.class,"say",String.class);
        Parameter parameter = method.getParameters()[0];
        System.out.println(parameter.getName());
    }
}
