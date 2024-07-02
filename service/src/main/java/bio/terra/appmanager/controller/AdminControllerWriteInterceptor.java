package bio.terra.appmanager.controller;

import bio.terra.appmanager.config.AdminControllerConfiguration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * This class is responsible for ensuring that authorized service accounts are the only requests
 * that can use the {@link AdminController} endpoints.
 *
 * <p>Ideally this allows for the {@link AdminController} to focus on its core functionality while
 * security is explicitly handled here.
 */
@Component
public class AdminControllerWriteInterceptor extends AdminControllerInterceptor {

  public AdminControllerWriteInterceptor(
      AdminControllerConfiguration adminControllerConfiguration) {
    super(adminControllerConfiguration.serviceAccountsForWrite());
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // DO NOT APPLY this interceptor to GET requests
    // meaning, if the request IS a GET, then let it pass
    if (request.getMethod().equals(HttpMethod.GET.name())) {
      return true;
    }

    return super.preHandle(request, response, handler);
  }
}
