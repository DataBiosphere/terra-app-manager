package bio.terra.appmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class AdminControllerInterceptorConfigurer implements WebMvcConfigurer {

  @Autowired AdminControllerWriteInterceptor adminControllerReadInterceptor;
  @Autowired AdminControllerWriteInterceptor adminControllerWriteInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(adminControllerReadInterceptor).addPathPatterns("/api/admin/**");
    registry.addInterceptor(adminControllerWriteInterceptor).addPathPatterns("/api/admin/**");
  }
}
