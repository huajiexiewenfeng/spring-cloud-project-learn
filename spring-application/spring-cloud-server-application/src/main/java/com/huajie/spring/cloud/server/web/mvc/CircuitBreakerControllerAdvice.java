package com.huajie.spring.cloud.server.web.mvc;

import com.huajie.spring.cloud.server.controller.ServerController;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.concurrent.TimeoutException;

//@RestControllerAdvice(assignableTypes = ServerController.class)
//public class CircuitBreakerControllerAdvice {
//
//    @ExceptionHandler
//   public void onTimeoutException(TimeoutException ex, Writer writer) throws IOException {
//        if (ex instanceof TimeoutException) {
//            writer.write(errorContent(""));
////            writer.flush();
////            writer.close();
//        }
//   }
//
//    public String errorContent(String message) {
//        return "Fault";
//    }
//}
