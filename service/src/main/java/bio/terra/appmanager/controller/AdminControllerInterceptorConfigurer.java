package bio.terra.appmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class AdminControllerInterceptorConfigurer implements WebMvcConfigurer {

  @Autowired AdminControllerInterceptor adminControllerInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminControllerInterceptor).addPathPatterns("/api/admin/**");
  }
}
