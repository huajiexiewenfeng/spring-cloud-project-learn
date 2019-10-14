package com.huajie.spring.cloud.client.annotation;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RequestMappingMethodInvocationHandler implements InvocationHandler {

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    private final String serviceName;

    private final BeanFactory beanFactory;

    public RequestMappingMethodInvocationHandler(String serviceName, BeanFactory beanFactory) {
        this.serviceName = serviceName;
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            String[] uri = requestMapping.value();
            StringBuilder urlBuilder = new StringBuilder("/").append(serviceName).append("/").append(uri[0]);
            //获取方法参数的数据
            int count = method.getParameterCount();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);

            StringBuilder queryString = new StringBuilder();

            for (int i = 0; i < count; i++) {
                Class<?> parameterType = parameterTypes[i];
                //获取方法参数上面的注解
                Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                RequestParam requestParam = (RequestParam)parameterAnnotations[i][0];

                if (requestParam != null) {
                    String value = requestParam.value();
                    String requestparamName = StringUtils.hasText(value) ? value : parameterNames[0];
                    String requestparamValue = String.class.equals(parameterType) ? (String) args[i] : String.valueOf(args[i]);
                    queryString.append("&").append(requestparamName).append("=").append(requestparamValue);
                }
            }

            if (StringUtils.hasText(queryString.toString())) {
                urlBuilder.append("?").append(queryString);
            }

            String url = urlBuilder.toString();
            RestTemplate restTemplate = beanFactory.getBean("customLoadBalanceRestTemplate", RestTemplate.class);

            return restTemplate.getForObject(url, method.getReturnType());
        }
        return null;
    }
}
