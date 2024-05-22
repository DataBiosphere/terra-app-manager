package bio.terra.appmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.appmanager.model.ChartVersion;
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
  void createChartVersion_withEmptyList() {
    chartService.createVersions(List.of());
    verifyNoInteractions(chartDao);
  }

  @Test
  void testCreateChartVersion_singleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHere";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);

    ArgumentCaptor<ChartVersion> argument = ArgumentCaptor.forClass(ChartVersion.class);

    chartService.createVersions(List.of(version1_1));
    verify(chartDao, times(1)).upsert(argument.capture());
    assertEquals(version1_1.chartName(), argument.getValue().chartName());
    assertEquals(version1_1.chartVersion(), argument.getValue().chartVersion());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testCreateChartVersion_multipleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHereOne";
    String chartVersion1_2 = "chartVersionHereToo";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = new ChartVersion(chartName1, chartVersion1_2);

    ArgumentCaptor<ChartVersion> argument = ArgumentCaptor.forClass(ChartVersion.class);

    InOrder inOrder = inOrder(chartDao);
    chartService.createVersions(List.of(version1_1, version1_2));
    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_1.chartName(), argument.getValue().chartName());
    assertEquals(version1_1.chartVersion(), argument.getValue().chartVersion());
    assertNull(argument.getValue().activeAt());

    inOrder.verify(chartDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_2.chartName(), argument.getValue().chartName());
    assertEquals(version1_2.chartVersion(), argument.getValue().chartVersion());
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
    chartService.getVersions(chartNameList, includeAll);
    inOrder.verify(chartDao, calls(1)).get(chartNameList, includeAll);
  }
}
