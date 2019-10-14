package com.huajie.spring.cloud.client.annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.stream.Stream;


public class RestClientsRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware , EnvironmentAware {

    private BeanFactory beanFactory;

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        ClassLoader classLoader = metadata.getClass().getClassLoader();
        //将@RestClient接口代理实现注册为Bean(@Autowired)
        //@RestClient->attributes->{clients:ServiceName}
        Map<String, Object> attributes = metadata.getAnnotationAttributes(EnableRestClients.class.getName());
        Class<?>[] clientsClasses = (Class<?>[]) attributes.get("clients");
        //筛选所有的接口
        Stream.of(clientsClasses)
                .filter(Class::isInterface)//过滤接口
                .filter(interfaceClass -> {//过滤@RestClient
//                    return interfaceClass.isAnnotationPresent(RestClient.class);
                    return AnnotationUtils.findAnnotation(interfaceClass, RestClient.class) != null;
                })
                .forEach(restClientClass -> {
                    //获取@RestClient元信息
                    RestClient restClient = AnnotationUtils.findAnnotation(restClientClass, RestClient.class);
                    //获取应用名称(处理占位符)
                    String serviceName = environment.resolvePlaceholders(restClient.name());
                    //目的-》使用restTemplate->serviceName/uri?param...
                    //@RestClient接口变成动态代理
                    Object proxy = Proxy.newProxyInstance(classLoader, new Class[]{restClientClass},
                            new RequestMappingMethodInvocationHandler(serviceName, beanFactory));
                    //注册bean
                    String beanName = "RestClient." + serviceName;
                    if(registry instanceof SingletonBeanRegistry){
                        SingletonBeanRegistry singletonBeanRegistry = (SingletonBeanRegistry)registry;
                        singletonBeanRegistry.registerSingleton(serviceName,proxy);
                    }
//                    registryBeanDefinition(beanName, registry, restClientClass, proxy);
                });
    }

    private void registryBeanDefinition(String beanName, BeanDefinitionRegistry registry, Class restClientClass, Object proxy) {
        BeanDefinition beanDefinition = null;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RestClientClassFactory.class);
        beanDefinitionBuilder.addConstructorArgValue(restClientClass);
        beanDefinitionBuilder.addConstructorArgValue(proxy);
        registry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
    }

    static class RestClientClassFactory implements FactoryBean {

        private final Class<?> restClientClass;

        private final Object proxy;

        public RestClientClassFactory(Class<?> restClientClass, Object proxy) {
            this.restClientClass = restClientClass;
            this.proxy = proxy;
        }

        @Override
        public Object getObject() throws Exception {
            return proxy;
        }

        @Override
        public Class<?> getObjectType() {
            return restClientClass;
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
