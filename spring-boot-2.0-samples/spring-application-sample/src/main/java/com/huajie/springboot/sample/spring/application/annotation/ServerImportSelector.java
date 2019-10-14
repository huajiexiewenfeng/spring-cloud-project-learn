package com.huajie.springboot.sample.spring.application.annotation;

import com.huajie.springboot.sample.spring.application.server.FtpServer;
import com.huajie.springboot.sample.spring.application.server.HttpServer;
import com.huajie.springboot.sample.spring.application.server.Server;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class ServerImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        //读取EnableServer所有的属性方法 本例中的方法为type()
        //key为方法的属性名 value为属性的值
        Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableServer.class.getName());
        //获取注解中设置的值
        Server.Type type = (Server.Type) annotationAttributes.get("type");
        //根据类型选择不同的服务器
        String[] importClassNames = new String[0];
        switch (type) {
            case HTTP:
                importClassNames = new String[]{HttpServer.class.getName()};
                break;
            case FTP:
                importClassNames = new String[]{FtpServer.class.getName()};
                break;
        }
        return importClassNames;
    }

}
