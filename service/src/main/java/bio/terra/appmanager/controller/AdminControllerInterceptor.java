package bio.terra.appmanager.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
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
public abstract class AdminControllerInterceptor implements HandlerInterceptor {

  private List<String> authorizedEmails;

  protected AdminControllerInterceptor(List<String> authorizedEmails) {
    this.authorizedEmails = authorizedEmails;
    if (authorizedEmails.isEmpty()) {
      throw new IllegalArgumentException("service_accounts configuration is required");
    }
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    var oauthEmail = request.getHeader("oauth2_claim_email");

    if (oauthEmail != null && authorizedEmails.contains(oauthEmail)) {
      return true;
    }

    response.sendError(
        HttpServletResponse.SC_FORBIDDEN,
        "Request did not come from an authorized Service Account");
    return false;
  }
}
