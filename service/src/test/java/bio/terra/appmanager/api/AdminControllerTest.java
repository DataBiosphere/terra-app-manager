package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.appmanager.controller.AdminController;
import bio.terra.appmanager.service.ChartService;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = AdminController.class)
@WebMvcTest
class AdminControllerTest {
  @MockBean ChartService serviceMock;

  @Autowired private MockMvc mockMvc;

  @Captor ArgumentCaptor<List<bio.terra.appmanager.model.ChartVersion>> capture_chartVersions;

  private AutoCloseable closeable;

  @BeforeEach
  public void open() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void release() throws Exception {
    closeable.close();
  }

  @Test
  void testCreate_204() throws Exception {
    String chartName = "chart-name-here";
    String chartVersion = "chart-version-here";

    mockMvc
        .perform(
            post("/api/admin/v1/charts/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"chartName\": \""
                        + chartName
                        + "\","
                        + "\"chartVersion\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNoContent());

    verify(serviceMock).createVersions(capture_chartVersions.capture());
    assert (capture_chartVersions.getValue().size() == 1);
    verifyChartVersion(
        capture_chartVersions.getValue().get(0), chartName, chartVersion, null, null, null);
  }

  @Test
  void testCreate_400() throws Exception {
    String chartName = "chart-name-here";
    String chartVersion = "chart-version-here";

    mockMvc
        .perform(
            post("/api/admin/v1/charts/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{" + "\"chartName\": \"" + chartName + "\"" + "}]"))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(serviceMock);
  }

  @Test
  @Disabled("Enable when Authorization is implemented")
  void testCreate_403() throws Exception {
    // we need to do this when we put in authorization
  }

  private void verifyChartVersion(
      bio.terra.appmanager.model.ChartVersion version,
      String chartName,
      String chartVersion,
      String appVersion,
      Date activeAt,
      Date inactiveAt) {
    assertEquals(version.chartName(), chartName);
    assertEquals(version.chartVersion(), chartVersion);
    assertEquals(version.appVersion(), appVersion);
    assertEquals(version.activeAt(), activeAt);
    assertEquals(version.inactiveAt(), inactiveAt);
  }
}
