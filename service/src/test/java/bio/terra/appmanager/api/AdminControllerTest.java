package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.appmanager.api.model.Chart;
import bio.terra.appmanager.api.model.ChartArray;
import bio.terra.appmanager.controller.AdminController;
import bio.terra.appmanager.controller.ChartNotFoundException;
import bio.terra.appmanager.controller.GlobalExceptionHandler;
import bio.terra.appmanager.model.ChartTestUtils;
import bio.terra.appmanager.service.ChartService;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = AdminController.class)
@WebMvcTest
class AdminControllerTest {
  @MockBean ChartService serviceMock;

  @Autowired AdminController controller;

  private MockMvc mockMvc;

  @Captor ArgumentCaptor<List<bio.terra.appmanager.model.Chart>> captureCharts;
  @Captor ArgumentCaptor<String> captureChartName;

  private AutoCloseable closeable;

  @BeforeEach
  public void setup() {
    closeable = MockitoAnnotations.openMocks(this);
    mockMvc =
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @AfterEach
  public void release() throws Exception {
    closeable.close();
  }

  @Test
  void testCreate_204() throws Exception {
    String chartName = "chart-name-here";
    String chartVersion = ChartTestUtils.makeChartVersion(0);

    mockMvc
        .perform(
            post("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNoContent());

    verify(serviceMock).createCharts(captureCharts.capture());
    assert (captureCharts.getValue().size() == 1);
    verifyChart(captureCharts.getValue().get(0), chartName, chartVersion, null, null, null);
  }

  @Test
  void testCreate_invalidChart() throws Exception {
    String chartName = "chart-name-here";
    String chartVersion = "invalid-chart-version$";

    mockMvc
        .perform(
            post("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreate_invalidChartName() throws Exception {
    String chartName = "invalidChartName$";
    String chartVersion = ChartTestUtils.makeChartVersion(0);

    mockMvc
        .perform(
            post("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testCreate_400() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(
            post("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[{" + "\"name\": \"" + chartName + "\"" + "}]"))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(serviceMock);
  }

  @Test
  void testGet_200_withNoParams() throws Exception {
    mockMvc.perform(get("/api/admin/v1/charts")).andExpect(status().isOk());

    verify(serviceMock).getCharts(List.of(), false);
  }

  @Test
  void testGet_200_withNameNoIncludeAll() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(get("/api/admin/v1/charts").queryParam("chartName", chartName))
        .andExpect(status().isOk());

    verify(serviceMock).getCharts(List.of(chartName), false);
  }

  @Test
  void testGet_200_withNameAndIncludeAll() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(
            get("/api/admin/v1/charts")
                .queryParam("chartName", chartName)
                .queryParam("includeAll", "true"))
        .andExpect(status().isOk());

    verify(serviceMock).getCharts(List.of(chartName), true);
  }

  @Test
  void testGet_200_WithNoNameAndIncludeAll() throws Exception {
    mockMvc
        .perform(get("/api/admin/v1/charts").queryParam("includeAll", "true"))
        .andExpect(status().isOk());

    verify(serviceMock).getCharts(List.of(), true);
  }

  @Test
  void testDelete_204() throws Exception {
    String chartName = "chart-name-here";

    mockMvc
        .perform(delete("/api/admin/v1/charts").queryParam("chartName", chartName))
        .andExpect(status().isNoContent());

    verify(serviceMock).deleteVersion(captureChartName.capture());
    assertEquals(captureChartName.getValue(), chartName);
  }

  @Test
  void testGet_ChartModelToApi() {
    String chartName = "chart-name-here";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    bio.terra.appmanager.model.Chart chart =
        new bio.terra.appmanager.model.Chart(chartName, chartVersion);
    chart = chart.activate(new Date()).inactivate(new Date());

    List<bio.terra.appmanager.model.Chart> chartNames = List.of(chart);

    when(serviceMock.getCharts(List.of(chartName), true)).thenReturn(chartNames);
    ChartArray chartArray = controller.getCharts(chartName, true).getBody();
    bio.terra.appmanager.api.model.Chart apiVersion = chartArray.get(0);

    assertEquals(1, chartArray.size());
    verifyChart(
        chart,
        apiVersion.getName(),
        apiVersion.getVersion(),
        apiVersion.getAppVersion(),
        apiVersion.getActiveAt(),
        apiVersion.getInactiveAt());
  }

  @Test
  void testUpdate_ControllerCall_200() {
    String chartName1 = "chart-name";
    String chartName2 = "chart-name2";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    String appVersion = "app.version";

    Chart apiChart1 =
        new bio.terra.appmanager.api.model.Chart()
            .name(chartName1)
            .version(chartVersion)
            .appVersion(appVersion);

    Chart apiChart2 =
        new bio.terra.appmanager.api.model.Chart()
            .name(chartName2)
            .version(chartVersion)
            .appVersion(appVersion);

    List<bio.terra.appmanager.model.Chart> chartNames =
        List.of(
            bio.terra.appmanager.model.Chart.fromApi(apiChart1),
            bio.terra.appmanager.model.Chart.fromApi(apiChart2));

    controller.updateChart(List.of(apiChart1, apiChart2));
    verify(serviceMock).updateVersions(chartNames);
  }

  @Test
  void testUpdate_ControllerCall_200EmptyList() {
    List<bio.terra.appmanager.model.Chart> chartNames = List.of();

    controller.updateChart(List.of());
    verify(serviceMock).updateVersions(chartNames);
  }

  @Test
  void testUpdate_201AllFields() throws Exception {
    String chartName1 = "chart-name";
    String chartVersion = ChartTestUtils.makeChartVersion(0);

    String appVersion = "app.version";

    mockMvc
        .perform(
            patch("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName1
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\","
                        + "\"appVersion\": \""
                        + appVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNoContent());
  }

  @Test
  void testUpdate_201NoOptionalFields() throws Exception {
    String chartName1 = "chart-name";
    String chartVersion = ChartTestUtils.makeChartVersion(0);

    mockMvc
        .perform(
            patch("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName1
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNoContent());
  }

  @Test
  void testUpdate_404() throws Exception {
    String chartName1 = "chart-name";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    bio.terra.appmanager.model.Chart chart =
        new bio.terra.appmanager.model.Chart(chartName1, chartVersion);

    doThrow(new ChartNotFoundException("test exception"))
        .when(serviceMock)
        .updateVersions(List.of(chart));

    mockMvc
        .perform(
            patch("/api/admin/v1/charts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"name\": \""
                        + chartName1
                        + "\","
                        + "\"version\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNotFound());
  }

  private void verifyChart(
      bio.terra.appmanager.model.Chart version,
      String chartName,
      String chartVersion,
      String appVersion,
      Date activeAt,
      Date inactiveAt) {
    assertEquals(version.name(), chartName);
    assertEquals(version.version(), chartVersion);
    assertEquals(version.appVersion(), appVersion);
    assertEquals(version.activeAt(), activeAt);
    assertEquals(version.inactiveAt(), inactiveAt);
  }
}
