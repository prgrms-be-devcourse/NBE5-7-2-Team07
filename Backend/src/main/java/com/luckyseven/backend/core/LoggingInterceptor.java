package com.luckyseven.backend.core;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    System.out.println("=========  Incoming Request Data =========");
    System.out.println("Request URL : " + request.getRequestURL());
    System.out.println("Request Method : " + request.getMethod());
    System.out.println("Query Params : " + request.getQueryString());
    return true;
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    System.out.println("=========  Outgoing Response Data =========");
    System.out.println("Response Status : " + response.getStatus());
  }
}
