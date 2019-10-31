package com.huajie.spring.cloud.config.client;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


@RefreshScope
@Controller
public class ConfigController implements PropertySourceLocator, EnvironmentAware, ApplicationContextAware {

    private Environment environment;
    private ApplicationContext applicationContext;

    @Value("${name}")
    private String name;

    @Value("${spring.cloud.config.uri}")
    private String serverUri;

    private RestTemplate restTemplate = new RestTemplate();

    private AtomicBoolean ready = new AtomicBoolean(false);

    @GetMapping("/config/hello")
    @ResponseBody
    public String hello() {
        return "hello" + name;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Scheduled(fixedRate = 1 * 5000)
    public void refreshConfig() {
        applicationContext.publishEvent(
                new RefreshEvent(this, null, "Refresh custom config"));
    }

    @Override
    public org.springframework.core.env.PropertySource<?> locate(Environment environment) {
        CompositePropertySource composite = new CompositePropertySource("configService");
        String url = serverUri + "/config/test/master";
        org.springframework.cloud.config.environment.Environment environmentObj = restTemplate.getForObject(url, org.springframework.cloud.config.environment.Environment.class);
        List<PropertySource> propertySources = environmentObj.getPropertySources();
        propertySources.stream().forEach(propertySource -> {
            Map<String, Object> source = (Map<String, Object>) propertySource.getSource();
            composite.addPropertySource(
                    new MapPropertySource(propertySource.getName(), source));
        });
        return composite;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
