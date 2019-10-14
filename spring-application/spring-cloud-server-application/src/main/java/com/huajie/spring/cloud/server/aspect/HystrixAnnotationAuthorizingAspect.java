package com.huajie.spring.cloud.server.aspect;

import com.huajie.spring.cloud.server.annotation.GPHystrixCommand;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.concurrent.*;

@Component
@Aspect
public class HystrixAnnotationAuthorizingAspect {

    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    private Semaphore semaphore = null;

    @Pointcut("@annotation(com.huajie.spring.cloud.server.annotation.GPHystrixCommand)")
    void anyRPCAnnotatedMethodCall() {
    }

    @Around("anyRPCAnnotatedMethodCall()")
    public Object executeAnnotatedMethod(ProceedingJoinPoint aJoinPoint) throws Throwable {
        BeforeAdviceMethodInvocationAdapter mi = BeforeAdviceMethodInvocationAdapter.createFrom(aJoinPoint);
        Method method = mi.getMethod();
        Object[] args = mi.getArguments();
        Object res = null;
        if (method.isAnnotationPresent(GPHystrixCommand.class)) {
            GPHystrixCommand annotation = method.getAnnotation(GPHystrixCommand.class);
            int timeout = annotation.timeout();
            int semaphoreValue = annotation.semaphore();
            String fallback = annotation.fallback();
            if (0 == timeout && 0 == semaphoreValue || 0 != timeout) {
                Future<Object> future = executorService.submit(() -> {
                    Object returnValue = null;
                    try {
                        returnValue = aJoinPoint.proceed(args);
                    } catch (Throwable throwable) {
                        throw new Exception(throwable);
                    }
                    return returnValue;
                });
                //100毫秒超时时间
                try {
                    res = future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | TimeoutException e) {
                    future.cancel(true);
                    res = invokeFallbackMethod(method, aJoinPoint.getTarget(), fallback, args);
                }
            }
            if (0 != semaphoreValue) {
                if (semaphore == null) {
                    semaphore = new Semaphore(semaphoreValue);
                }
                try {
                    semaphore.acquire();
                    res = aJoinPoint.proceed(args);
                } finally {
                    semaphore.release();
                }
            }
            return res;
        }
        return null;
    }

    private Object invokeFallbackMethod(Method method, Object bean, String fallback, Object[] arguments) throws Exception {
        // 查找 fallback 方法
        Method fallbackMethod = findFallbackMethod(method, bean, fallback);
        return fallbackMethod.invoke(bean, arguments);
    }

    private Method findFallbackMethod(Method method, Object bean, String fallbackMethodName) throws
            NoSuchMethodException {
        // 通过被拦截方法的参数类型列表结合方法名，从同一类中找到 fallback 方法
        Class beanClass = bean.getClass();
        Method fallbackMethod = beanClass.getMethod(fallbackMethodName, method.getParameterTypes());
        return fallbackMethod;
    }

    @PreDestroy
    private void destroy() {
        executorService.shutdown();
    }
}