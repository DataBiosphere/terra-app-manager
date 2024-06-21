package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import bio.terra.appmanager.controller.AdminController;
import bio.terra.appmanager.controller.AdminControllerInterceptor;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Confirms that the {@link AdminControllerInterceptor} is protecting the {@link AdminController}
 * and not other endpoints
 */
@SpringBootTest()
class AdminControllerInterceptorConfigTest {

  @Autowired private RequestMappingHandlerMapping requestMappingHandlerMapping;

  private static HttpMethod getRequestMethod(RequestMappingInfo requestMappingInfo) {
    assertTrue(
        !requestMappingInfo.getMethodsCondition().isEmpty(),
        "assume admin endpoints have HTTP verb");
    Set<RequestMethod> httpMethods = requestMappingInfo.getMethodsCondition().getMethods();
    assertTrue(httpMethods.size() == 1, "assume admin endpoints have HTTP verb");
    return httpMethods.iterator().next().asHttpMethod();
  }

  @NotNull
  private static String getAdminRequestUri(
      RequestMappingInfo requestMappingInfo, HttpMethod method) {
    String requestUri = requestMappingInfo.getActivePatternsCondition().toString();
    requestUri = requestUri.substring(1, requestUri.length() - 1);
    return requestUri;
  }

  @Test
  public void validateRequiredForAdminEndpoints() {
    requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
        .filter(
            requestMappingInfo -> {
              return requestMappingInfo.getActivePatternsCondition().toString().contains("/admin");
            })
        .forEach(
            requestMappingInfo -> {
              HttpMethod method = getRequestMethod(requestMappingInfo);

              // the toString() of the patterns condition is surrounded by [...]
              String requestUri = getAdminRequestUri(requestMappingInfo, method);

              MockServletContext context = new MockServletContext();
              MockHttpServletRequest request =
                  new MockHttpServletRequest(context, method.name(), requestUri);

              // this is needed to match the POST and PATCH requests
              request.setContentType(MediaType.APPLICATION_JSON_VALUE);

              try {
                HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);

                assert chain != null;
                Optional<HandlerInterceptor> adminInterceptor =
                    chain.getInterceptorList().stream()
                        .filter(AdminControllerInterceptor.class::isInstance)
                        .findFirst();

                assertTrue(adminInterceptor.isPresent());
              } catch (Exception e) {
                e.printStackTrace();
                fail("Unexpected exception was thrown");
              }
            });
  }

  @Test
  public void validateNonAdminEndpoints() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/status");

    HandlerExecutionChain chain = requestMappingHandlerMapping.getHandler(request);

    assert chain != null;
    Optional<HandlerInterceptor> adminInterceptor =
        chain.getInterceptorList().stream()
            .filter(AdminControllerInterceptor.class::isInstance)
            .findFirst();

    assertTrue(adminInterceptor.isEmpty());
  }
}
