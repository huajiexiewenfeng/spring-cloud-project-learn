package com.huajie.spring.cloud.client.service.rest.clients;

import com.huajie.spring.cloud.client.annotation.RestClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.stream.Stream;

@RestClient(name = "${saying.rest.service.name}")
public interface SayingRestService {

    @RequestMapping("say")
    public String say(@RequestParam("message") String message);

    public static void main(String[] args) {
        Method method = ReflectionUtils.findMethod(SayingRestService.class,"say",String.class);
        ParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
        String[] parameterNames = discoverer.getParameterNames(method);
        Stream.of(discoverer.getParameterNames(method)).forEach(System.out::println);
    }

}
