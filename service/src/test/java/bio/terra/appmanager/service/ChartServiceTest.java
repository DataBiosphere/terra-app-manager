package bio.terra.appmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import bio.terra.appmanager.BaseSpringBootTest;
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
  void testUpdateVersion() {
    String chartName1 = "chart-name-here";
    String chartVersion = "chartVersion";
    Chart chart = new Chart(chartName1, chartVersion);

    chartService.updateVersions(List.of(chart));
    verify(chartDao, times(1)).upsert(chart);
  }
}
