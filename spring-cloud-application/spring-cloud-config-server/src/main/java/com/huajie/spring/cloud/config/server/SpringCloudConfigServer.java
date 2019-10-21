package com.huajie.spring.cloud.config.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigServer
public class SpringCloudConfigServer {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServer.class, args);
    }

    @Value("${spring.cloud.config.file.path}")
    public String filePath;

    @Bean
    public EnvironmentRepository environmentRepository() {
        return (String application, String profile, String label) -> {
            Environment environment = new Environment("default", profile);
            List<PropertySource> propertySources = environment.getPropertySources();

            Map<String, Object> source = getSource();

            PropertySource propertySource = new PropertySource("map", source);
            propertySources.add(propertySource);
            return environment;
        };
    }

    private Map<String, Object> getSource() {
        Map<String, Object> source = new HashMap<>();
        File file = new File(filePath);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            Stream.of(files).forEach(textFile -> {
                BufferedReader reader = null;
                String tempString = null;
                int line = 1;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), "GBK"));
                    while ((tempString = reader.readLine()) != null) {
                        System.out.println("Line" + line + ":" + tempString);
                        String[] strs = StringUtils.split(tempString, "=");
                        source.put(strs[0], strs[1]);
                        line++;
                    }
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return source;
    }

}

