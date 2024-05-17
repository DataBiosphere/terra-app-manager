package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.appmanager.api.model.ChartArray;
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

  @Autowired AdminController controller;

  @Captor ArgumentCaptor<List<bio.terra.appmanager.model.ChartVersion>> capture_chartVersions;
  @Captor ArgumentCaptor<List<String>> capture_chartNames;

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

    mockMvc
        .perform(
            post("/api/admin/v1/charts/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{" + "\"chartName\": \"" + chartName + "\"" + "}]"))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(serviceMock);
  }

  @Test
  void testGet_200_withNoParams() throws Exception {
    mockMvc.perform(get("/api/admin/v1/charts/versions")).andExpect(status().isOk());

    verify(serviceMock).getVersions(List.of(), false);
  }

  @Test
  void testGet_200_withNameNoIncludeAll() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(get("/api/admin/v1/charts/versions").queryParam("chartName", chartName))
        .andExpect(status().isOk());

    verify(serviceMock).getVersions(List.of(chartName), false);
  }

  @Test
  void testGet_200_withNameAndIncludeAll() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(
            get("/api/admin/v1/charts/versions")
                .queryParam("chartName", chartName)
                .queryParam("includeAll", "true"))
        .andExpect(status().isOk());

    verify(serviceMock).getVersions(List.of(chartName), true);
  }

  @Test
  void testGet_200_WithNoNameAndIncludeAll() throws Exception {
    mockMvc
        .perform(get("/api/admin/v1/charts/versions").queryParam("includeAll", "true"))
        .andExpect(status().isOk());

    verify(serviceMock).getVersions(List.of(), true);
  }

  @Test
  @Disabled("Enable when Authorization is implemented")
  void testGet_403() throws Exception {
    // we need to do this when we put in authorization
    // this will fail if someone removes @Disabled(...)
    fail("force whomever removes @Disabled(...) to implement test");
  }

  @Test
  @Disabled("Enable when Authorization is implemented")
  void testCreate_403() throws Exception {
    // we need to do this when we put in authorization
    // this will fail if someone removes @Disabled(...)
    fail("force whomever removes @Disabled(...) to implement test");
  }

  @Test
  void testDelete_204() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(delete("/api/admin/v1/charts/versions").queryParam("chartNames", chartName))
        .andExpect(status().isNoContent());

    verify(serviceMock).deleteVersions(capture_chartNames.capture());
    assert (capture_chartNames.getValue().size() == 1);
    assertEquals(capture_chartNames.getValue().get(0), chartName);
  }

  @Test
  void testDelete_204_multipleChartNames() throws Exception {
    String chartName1 = "chart-name-1";
    String chartName2 = "chart-name-2";

    mockMvc
        .perform(
            delete("/api/admin/v1/charts/versions")
                .queryParam("chartNames", chartName1, chartName2))
        .andExpect(status().isNoContent());

    verify(serviceMock).deleteVersions(capture_chartNames.capture());
    assert (capture_chartNames.getValue().size() == 2);
    assertEquals(capture_chartNames.getValue().get(0), chartName1);
    assertEquals(capture_chartNames.getValue().get(1), chartName2);
  }

  @Test
  @Disabled("Enable when Authorization is implemented")
  void testDelete_403() throws Exception {
    // we need to do this when we put in authorization
    // this will fail if someone removes @Disabled(...)
    fail("force whomever removes @Disabled(...) to implement test");
  }

  @Test
  void testGet_ChartVersionModelToApi() {
    String chartName = "chart-name-here";
    String chartVersion = "chart-version";
    bio.terra.appmanager.model.ChartVersion chart =
        new bio.terra.appmanager.model.ChartVersion(chartName, chartVersion);
    chart = chart.activate(new Date()).inactivate(new Date());

    List<bio.terra.appmanager.model.ChartVersion> chartNames = List.of(chart);

    when(serviceMock.getVersions(List.of(chartName), true)).thenReturn(chartNames);
    ChartArray chartArray = controller.getChartVersions(chartName, true).getBody();
    bio.terra.appmanager.api.model.ChartVersion apiVersion = chartArray.get(0);

    assertEquals(chartArray.size(), 1);
    verifyChartVersion(
        chart,
        apiVersion.getChartName(),
        apiVersion.getChartVersion(),
        apiVersion.getAppVersion(),
        apiVersion.getActiveAt(),
        apiVersion.getInactiveAt());
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
