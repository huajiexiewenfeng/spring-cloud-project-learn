package com.huajie.spring.cloud.server.web.mvc;

import com.huajie.spring.cloud.server.annotation.Timeout;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class CircuitBreakerHandlerInterceptor implements HandlerInterceptor {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
//                                Object handler, @Nullable Exception ex) throws Exception {
//        if ("/sayLevel2".equals(request.getRequestURI()) && ex instanceof TimeoutException) {
//            PrintWriter writer = response.getWriter();
//            writer.write(errorContent(""));
//        }
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Timeout timeOut = handlerMethod.getMethodAnnotation(Timeout.class);
            if (null != timeOut) {
                int timeout = timeOut.timeout();

                String fallback = timeOut.fallback();
                Object bean = handlerMethod.getBean();
                Method method = handlerMethod.getMethod();


                Map<String, String[]> parameterMap = request.getParameterMap();
                Class<?>[] parameterTypes = method.getParameterTypes();
                //实参
                Object[] paramValues = new Object[parameterTypes.length];

                int i=0;
                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "")
                            .replaceAll("\\s", ",");
                    paramValues[i] = caseStringValue(value, parameterTypes[i]);
                    i++;
                }

                Future<Object> future = executorService.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return method.invoke(bean,paramValues);
                    }
                });
                Object returnValue = null;
                try {
                    returnValue = future.get(timeout, TimeUnit.MILLISECONDS);
                } catch (TimeoutException e) {
                    if (StringUtils.hasText(fallback)) {
                        returnValue = invokeFallbackMethod(handlerMethod, fallback,paramValues);
                    }else{
                        returnValue="server is busy";
                    }

                }
                response.getWriter().write(String.valueOf(returnValue));
                return false;
            }
        }
        return false;
    }

    private Object invokeFallbackMethod(HandlerMethod handlerMethod, String fallback, Object[] paramValues) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object bean = handlerMethod.getBean();
        //fallback方法签名要和原方法保持一致（参数类型和个数相同）
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        Class[] parameterTypes = Stream.of(methodParameters).map(MethodParameter::getParameterType)
                .toArray(Class[]::new);
        Method method = bean.getClass().getMethod(fallback, parameterTypes);
        return method.invoke(bean,paramValues);
    }

    private Object caseStringValue(String value, Class<?> parameterType) {
        if (Integer.class == parameterType) {
            return Integer.valueOf(value);
        } else if (Double.class == parameterType) {
            return Double.valueOf(value);
        } else if (String.class == parameterType) {
            return String.valueOf(value);
        } else {
            if (value != null) {
                return value;
            } else {
                return null;
            }
        }
    }
}
