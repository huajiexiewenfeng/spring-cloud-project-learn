package com.huajie.spring.cloud.server.controller;

import com.huajie.spring.cloud.server.annotation.GPHystrixCommand;
import com.huajie.spring.cloud.server.annotation.Timeout;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.*;

@RestController
public class ServerController {

    private final Environment environment;

    private final static Random random = new Random();

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public ServerController(Environment environment) {
        this.environment = environment;
    }

    public String getPort() {
        return environment.getProperty("local.server.port");
    }

    @HystrixCommand(
            fallbackMethod = "errorContent",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                            value = "100")
            }
    )
    @GetMapping("/say")
    public String say(@RequestParam("message") String message) throws InterruptedException {
        int value = random.nextInt(200);
        System.out.println("say() cost " + value + "ms");
        Thread.sleep(value);
        System.out.println("port:" + getPort() + ",接收到消息-say:" + message);
        return "port:" + getPort() + ",Hello," + message;
    }

    /**
     * 简易版本
     *
     * @param message
     * @return
     * @throws InterruptedException
     */
    @GetMapping("/sayLevel1")
    public String sayLevel1(@RequestParam("message") String message) throws Exception {
        Future<String> future = executorService.submit(() -> {
            return await(message);
        });
        String res = null;
        //100毫秒超时时间
        try {
            res = future.get(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | TimeoutException e) {
            return errorContent(message);
        }
        return res;
    }

    private String await(String message) throws InterruptedException {
        int value = random.nextInt(200);
        System.out.println("say() cost " + value + "ms");
        Thread.sleep(value);
        System.out.println("port:" + getPort() + ",接收到消息-say:" + message);
        return "port:" + getPort() + ",Hello," + message;
    }

    /**
     * 中级版本
     *
     * @param message
     * @return
     * @throws Exception
     */
    @GetMapping("/sayLevel2")
    public String sayLevel2(@RequestParam("message") String message) throws Exception {
        Future<String> future = executorService.submit(() -> {
            return await(message);
        });
        String res = null;
        try {
            res = future.get(100, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        }
        return res;
    }

    /**
     * 高级版本
     *
     * @param message
     * @return
     * @throws Exception
     */
    @Timeout(timeout = 150,fallback = "errorContent")
    @GetMapping("/sayLevelTimeout")
    public String sayLevelTimeout(@RequestParam("message") String message) throws Exception {
        return await(message);
    }

    /**
     * 高级版本
     *
     * @param message
     * @return
     * @throws Exception
     */
    @GPHystrixCommand(timeout = 150,fallback = "errorContent")
    @GetMapping("/sayLevel3")
    public String sayLevel3(@RequestParam("message") String message) throws Exception {
        return await(message);
    }


    /**
     * 高级版本
     *
     * @param message
     * @return
     * @throws Exception
     */
    @GPHystrixCommand(semaphore = 5)
    @GetMapping("/sayLevel4")
    public String sayLevel4(@RequestParam("message") String message) throws Exception {
        return await(message);
    }

    public String errorContent(String message) {
        return "Fault";
    }

}
