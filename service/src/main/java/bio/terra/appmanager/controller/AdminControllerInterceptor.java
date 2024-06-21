package bio.terra.appmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * This class is responsible for ensuring that authorized service accounts are the only requests
 * that can use the {@link AdminController} endpoints.
 *
 * <p>Ideally this allows for the {@link AdminController} to focus on its core functionality while
 * security is explicitly handled here.
 */
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

    if (!authorizedEmails.contains(oauthEmail)) {
      response.sendError(
          HttpServletResponse.SC_FORBIDDEN,
          "Request did not come from an authorized Service Account");
      return false;
    } else {

    }

    return true;
  }
}
