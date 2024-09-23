package bio.terra.appmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.config.ChartServiceConfiguration;
import bio.terra.appmanager.controller.ChartNotFoundException;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.appmanager.events.ChartEvents;
import bio.terra.appmanager.model.Chart;
import bio.terra.appmanager.model.ChartTestUtils;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

class ChartServiceTest extends BaseSpringBootTest {

  @MockBean ChartDao chartDao;

  @Autowired
  @Qualifier("mockService")
  ChartService chartService;

  @Test
  void testCreateChart_withEmptyList_chartDao() {
    chartService.createCharts(List.of());
    verifyNoInteractions(chartDao);
  }

  @Test
  void testCreateChart_unknownChartName() {
    String chart1Name = "unknown-chart";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);
    List<Chart> chartList = List.of(chart1);

    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> chartService.createCharts(chartList));

    assertTrue(exception.getMessage().contains("unrecognized chartName provided"));
  }

  @Test
  void testCreateChart_singleElement_chartDao() {
    String chart1Name = "chart-name-here";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);

    ArgumentCaptor<Chart> argument = ArgumentCaptor.forClass(Chart.class);

    chartService.createCharts(List.of(chart1));
    verify(chartDao, times(1)).upsert(argument.capture());
    assertEquals(chart1.name(), argument.getValue().name());
    assertEquals(chart1.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testCreateChart_singleElement_publisherDao() {
    String chart1Name = "chart-name-here";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);

    chartService.createCharts(List.of(chart1));
    // TODO
    //    verify(publisherDao, times(1)).publish("chart created");
  }

  @Test
  void testCreateChart_multipleElement_chartDao() {
    String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    String chart1Version2 = ChartTestUtils.makeChartVersion(1);
    Chart oldChart1 = new Chart(chart1Name, chart1Version1);
    Chart newChart1 = new Chart(chart1Name, chart1Version2);

    ArgumentCaptor<Chart> argument = ArgumentCaptor.forClass(Chart.class);

    InOrder inOrder = inOrder(chartDao);
    chartService.createCharts(List.of(oldChart1, newChart1));
    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(oldChart1.name(), argument.getValue().name());
    assertEquals(oldChart1.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());

    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(newChart1.name(), argument.getValue().name());
    assertEquals(newChart1.version(), argument.getValue().version());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testCreateChart_multipleElement_publisherDao() {
    String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    String chart1Version2 = ChartTestUtils.makeChartVersion(1);
    Chart oldChart1 = new Chart(chart1Name, chart1Version1);
    Chart newChart1 = new Chart(chart1Name, chart1Version2);

    chartService.createCharts(List.of(oldChart1, newChart1));
    // TODO
    //    inOrderPublish.verify(publisherDao, calls(1)).publish("chart created");
    //    inOrderPublish.verify(publisherDao, calls(1)).publish("chart created");
  }

  @Test
  void testDeleteVersion_chartDao() {
    String chart1Name = "chart-name-here";

    ArgumentCaptor<List<String>> argument = ArgumentCaptor.forClass(List.class);

    chartService.deleteVersion(chart1Name);
    verify(chartDao, times(1)).delete(argument.capture());
    assertEquals(1, argument.getValue().size());
    assertEquals(chart1Name, argument.getValue().get(0));
  }

  @Test
  void testDeleteVersion_publisherDao() {
    String chart1Name = "chart-name-here";

    chartService.deleteVersion(chart1Name);
    // TODO
    //    verify(publisherDao, times(1)).publish("chart deleted");
  }

  @Test
  void testGetVersions() {
    List<String> chartNameList = List.of("chart-name-here");
    Boolean includeAll = true;

    InOrder inOrder = inOrder(chartDao);
    chartService.getCharts(chartNameList, includeAll);
    inOrder.verify(chartDao, calls(1)).get(chartNameList, includeAll);
  }

  @Test
  void testUpdateVersions_chartDao() {
    String chart1Name = "chart-name-here";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);

    when(chartDao.get(List.of(chart1Name), true)).thenReturn(List.of(chart1));

    chartService.updateVersions(List.of(chart1));
    verify(chartDao, times(1)).upsert(chart1);
  }

  @Test
  void testUpdateVersions_publisherDao() {
    String chart1Name = "chart-name-here";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);

    when(chartDao.get(List.of(chart1Name), true)).thenReturn(List.of(chart1));

    chartService.updateVersions(List.of(chart1));
    // TODO
    //    verify(publisherDao, times(1)).publish("chart updated");
  }

  @Test
  void testUpdateChart_unknownChartName() {
    String chart1Name = "unknown-chart";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);
    List<Chart> chartList = List.of(chart1);

    Exception exception =
        assertThrows(IllegalArgumentException.class, () -> chartService.updateVersions(chartList));

    assertTrue(exception.getMessage().contains("unrecognized chartName provided"));
  }

  @Test
  void testUpdateVersions_chartNotFound() {
    String chart1Name = "chart-name";
    String chart1Version = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version);
    List<Chart> charts = List.of(chart1);

    assertThrows(ChartNotFoundException.class, () -> chartService.updateVersions(charts));
  }

  @Test
  void testUpdate_multipleChartNotFound() {
    String chart1Name = "chart-name";
    String chart2Name = "chart-name2";
    String chart3Name = "chart-name3";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chartVersion);
    Chart chart2 = new Chart(chart2Name, chartVersion);
    Chart chart3 = new Chart(chart3Name, chartVersion);
    List<Chart> charts = List.of(chart1, chart2, chart3);
    List<String> notPresentCharts = List.of(chart2Name, chart3Name);

    when(chartDao.get(List.of(chart1Name), true)).thenReturn(List.of(chart1));

    ChartNotFoundException ex =
        assertThrows(ChartNotFoundException.class, () -> chartService.updateVersions(charts));

    assertEquals(
        "The chart(s) you attempted to update do not currently exist, please create first: "
            + notPresentCharts,
        ex.getMessage());
  }

  @TestConfiguration
  public static class MockChartServiceConfiguration {
    @Bean(name = "mockService")
    public ChartService getChartService(ChartDao chartDao, ChartEvents chartPublisher) {
      return new ChartService(
          new ChartServiceConfiguration(
              List.of("chart-name-here", "chart-name", "chart-name2", "chart-name3")),
          chartDao,
          chartPublisher);
    }
  }
}
