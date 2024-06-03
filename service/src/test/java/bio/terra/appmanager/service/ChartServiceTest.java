package bio.terra.appmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.controller.ChartNotFoundException;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.appmanager.model.Chart;
import bio.terra.appmanager.model.ChartTestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ChartServiceTest extends BaseSpringBootTest {
  @MockBean ChartDao chartDao;

  @Autowired ChartService chartService;

  @Test
  void createChart_withEmptyList() {
    chartService.createCharts(List.of());
    verifyNoInteractions(chartDao);
  }

  @Test
  void testCreateChart_singleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = ChartTestUtils.makeChartVersion(0);
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);

    ArgumentCaptor<Chart> argument = ArgumentCaptor.forClass(Chart.class);

    chartService.createCharts(List.of(version1_1));
    verify(chartDao, times(1)).upsert(argument.capture());
    assertEquals(version1_1.name(), argument.getValue().name());
    assertEquals(version1_1.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testCreateChart_multipleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = ChartTestUtils.makeChartVersion(0);
    String chartVersion1_2 = ChartTestUtils.makeChartVersion(1);
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);
    Chart version1_2 = new Chart(chartName1, chartVersion1_2);

    ArgumentCaptor<Chart> argument = ArgumentCaptor.forClass(Chart.class);

    InOrder inOrder = inOrder(chartDao);
    chartService.createCharts(List.of(version1_1, version1_2));
    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_1.name(), argument.getValue().name());
    assertEquals(version1_1.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());

    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_2.name(), argument.getValue().name());
    assertEquals(version1_2.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testDeleteVersion() {
    String chartName1 = "chart-name-here";

    ArgumentCaptor<List<String>> argument = ArgumentCaptor.forClass(List.class);

    chartService.deleteVersion(chartName1);
    verify(chartDao, times(1)).delete(argument.capture());
    assertEquals(1, argument.getValue().size());
    assertEquals(chartName1, argument.getValue().get(0));
  }

  @Test
  void testGetVersions() {
    List<String> chartNameList = List.of("chart-name-here");
    Boolean includeAll = true;

    ArgumentCaptor<List<String>> argument1 = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<Boolean> argument2 = ArgumentCaptor.forClass(Boolean.class);

    InOrder inOrder = inOrder(chartDao);
    chartService.getCharts(chartNameList, includeAll);
    inOrder.verify(chartDao, calls(1)).get(chartNameList, includeAll);
  }

  @Test
  void testUpdateVersions() {
    String chartName1 = "chart-name-here";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    Chart chart = new Chart(chartName1, chartVersion);

    when(chartDao.get(List.of(chartName1), true)).thenReturn(List.of(chart));

    chartService.updateVersions(List.of(chart));
    verify(chartDao, times(1)).upsert(chart);
  }

  @Test
  void testUpdateVersions_ChartNotFound() {
    String chartName = "chart-name";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    Chart chart = new Chart(chartName, chartVersion);

    assertThrows(ChartNotFoundException.class, () -> chartService.updateVersions(List.of(chart)));
  }

  @Test
  void testUpdate_ControllerCall_multiple404() {
    String chartName1 = "chart-name";
    String chartName2 = "chart-name2";
    String chartName3 = "chart-name3";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chartName1, chartVersion);
    Chart chart2 = new Chart(chartName2, chartVersion);
    Chart chart3 = new Chart(chartName3, chartVersion);
    List<Chart> charts = List.of(chart1, chart2, chart3);
    List<String> notPresentCharts = List.of(chartName2, chartName3);

    when(chartDao.get(List.of(chartName1), true)).thenReturn(List.of(chart1));

    ChartNotFoundException ex =
        assertThrows(
            ChartNotFoundException.class,
            () -> chartService.updateVersions(charts));
    assertEquals(
        "The chart(s) you attempted to update do not currently exist, please create first: "
            + notPresentCharts,
        ex.getMessage());
  }
}
