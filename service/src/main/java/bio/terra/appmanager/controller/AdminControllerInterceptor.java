package bio.terra.appmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminControllerInterceptor implements HandlerInterceptor {
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    var authorizedEmails =
        Arrays.asList(
            "appmanager-dev@broad-dsde-dev.iam.gserviceaccount.com",
            "leonardo-dev@broad-dsde-dev.iam.gserviceaccount.com");

    var oauthEmail = request.getHeader("oauth2_claim_email");

    System.out.println("oauthEmail: " + oauthEmail);
    return (!isAdminPath(request)
        || (isAdminPath(request) && oauthEmail != null && authorizedEmails.contains(oauthEmail)));
  }

  private boolean isAdminPath(HttpServletRequest request) {
    String urlPath = request.getRequestURI();
    return urlPath != null && urlPath.contains("admin/");
  }
}
