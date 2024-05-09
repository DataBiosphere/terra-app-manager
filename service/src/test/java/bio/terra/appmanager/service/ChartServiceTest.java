package bio.terra.appmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.dao.ChartVersionDao;
import bio.terra.appmanager.model.ChartVersion;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ChartServiceTest extends BaseSpringBootTest {
  @MockBean ChartVersionDao chartVersionDao;

  @Autowired ChartService chartService;

  @Test
  void createChartVersion_withEmptyList() {
    chartService.createVersions(List.of());
    verifyNoInteractions(chartVersionDao);
  }

  @Test
  void testCreateChartVersion_singleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);

    ArgumentCaptor<ChartVersion> argument = ArgumentCaptor.forClass(ChartVersion.class);

    chartService.createVersions(List.of(version1_1));
    verify(chartVersionDao, times(1)).upsert(argument.capture());
    assertEquals(version1_1.chartName(), argument.getValue().chartName());
    assertEquals(version1_1.chartVersion(), argument.getValue().chartVersion());
    assertNull(argument.getValue().activeAt());
  }

  @Test
  void testCreateChartVersion_multipleElement() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = new ChartVersion(chartName1, chartVersion1_2);

    ArgumentCaptor<ChartVersion> argument = ArgumentCaptor.forClass(ChartVersion.class);

    InOrder inOrder = inOrder(chartVersionDao);
    chartService.createVersions(List.of(version1_1, version1_2));
    inOrder.verify(chartVersionDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_1.chartName(), argument.getValue().chartName());
    assertEquals(version1_1.chartVersion(), argument.getValue().chartVersion());
    assertNull(argument.getValue().activeAt());

    inOrder.verify(chartVersionDao, calls(1)).upsert(argument.capture());
    assertEquals(version1_2.chartName(), argument.getValue().chartName());
    assertEquals(version1_2.chartVersion(), argument.getValue().chartVersion());
    assertNull(argument.getValue().activeAt());
  }
}