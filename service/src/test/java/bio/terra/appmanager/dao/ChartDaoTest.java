package bio.terra.appmanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import bio.terra.appmanager.model.Chart;
import bio.terra.appmanager.model.ChartTestUtils;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class ChartDaoTest extends BaseDaoTest {
  @Autowired ChartDao chartDao;
  @MockBean PublisherDao publisherDao;

  @Test
  void testSingleVersionUpsert() {
    String chartName = "chart-name-here";
    String chartVersion = ChartTestUtils.makeChartVersion(0);
    Chart version = new Chart(chartName, chartVersion);

    chartDao.upsert(version);
    List<Chart> storedVersions = chartDao.get(List.of(chartName), false);

    assertEquals(1, storedVersions.size());
    Chart storedVersion = storedVersions.get(0);
    assertEquals(chartName, storedVersion.name());
    assertNotEquals(version.activeAt(), storedVersion.activeAt());
  }

  @Test
  void testMultiVersionUpsert() {
    String chartName = "chart-name-here";

    String chartVersion1 = ChartTestUtils.makeChartVersion(0);
    Chart version1 = new Chart(chartName, chartVersion1);

    String chartVersion2 = ChartTestUtils.makeChartVersion(1);
    Chart version2 = new Chart(chartName, chartVersion2);

    chartDao.upsert(version1);
    chartDao.upsert(version2);
    List<Chart> storedVersions = chartDao.get(List.of(chartName), true);

    assertEquals(2, storedVersions.size());
    Chart targetVersion = getByChart(storedVersions, chartVersion1);
    assertNotNull(targetVersion);
    assertEquals(version1.version(), targetVersion.version());
    assertNotNull(targetVersion.inactiveAt());

    targetVersion = getByChart(storedVersions, chartVersion2);
    assertNotNull(targetVersion);
    assertEquals(version2.version(), targetVersion.version());
    assertNull(targetVersion.inactiveAt());
  }

  @Test
  void testMultiNameGet() {
    String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    String chart1Version2 = ChartTestUtils.makeChartVersion(1);
    Chart oldChart1 = new Chart(chart1Name, chart1Version1);
    Chart newChart1 = new Chart(chart1Name, chart1Version2);

    String chart2Name = "chart-name-here-too";
    String chart2Version1 = ChartTestUtils.makeChartVersion(2);
    String chart2Version2 = ChartTestUtils.makeChartVersion(3);
    Chart oldChart2 = new Chart(chart2Name, chart2Version1);
    Chart newChart2 = new Chart(chart2Name, chart2Version2);

    String chart3Name = "chart-name";
    String chart3Version1 = ChartTestUtils.makeChartVersion(4);
    String chart3Version2 = ChartTestUtils.makeChartVersion(5);
    Chart oldChart3 = new Chart(chart3Name, chart3Version1);
    Chart newChart3 = new Chart(chart3Name, chart3Version2);

    chartDao.upsert(oldChart1);
    chartDao.upsert(newChart1);
    chartDao.upsert(oldChart2);
    chartDao.upsert(newChart2);
    chartDao.upsert(oldChart3);
    chartDao.upsert(newChart3);

    List<Chart> storedVersions = chartDao.get((List.of(chart1Name, chart2Name)));
    assertEquals(2, storedVersions.size());

    List<Chart> targetCharts =
        storedVersions.stream().filter(version -> chart1Name.equals(version.name())).toList();
    assertEquals(1, targetCharts.size());
    Chart targetVersion = targetCharts.get(0);
    assertEquals(chart1Version2, targetVersion.version());

    targetCharts =
        storedVersions.stream().filter(version -> chart2Name.equals(version.name())).toList();
    assertEquals(1, targetCharts.size());
    targetVersion = targetCharts.get(0);
    assertEquals(chart2Version2, targetVersion.version());
  }

  @Test
  void testGetAll() {
    String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    String chart1Version2 = ChartTestUtils.makeChartVersion(1);
    Chart oldChart1 = new Chart(chart1Name, chart1Version1);
    Chart newChart1 = new Chart(chart1Name, chart1Version2);

    String chart2Name = "chart-name-here-too";
    String chart2Version1 = ChartTestUtils.makeChartVersion(3);
    String chart2Version2 = ChartTestUtils.makeChartVersion(4);
    Chart oldChart2 = new Chart(chart2Name, chart2Version1);
    Chart newChart2 = new Chart(chart2Name, chart2Version2);

    String chart3Name = "chart-name-here-again";
    String chart3Version1 = ChartTestUtils.makeChartVersion(5);
    String chart3Version2 = ChartTestUtils.makeChartVersion(6);
    Chart oldChart3 = new Chart(chart3Name, chart3Version1);
    Chart newChart3 = new Chart(chart3Name, chart3Version2);

    chartDao.upsert(oldChart1);
    chartDao.upsert(newChart1);
    chartDao.upsert(oldChart2);
    chartDao.upsert(newChart2);
    chartDao.upsert(oldChart3);
    chartDao.upsert(newChart3);

    List<Chart> storedVersions = chartDao.get(true);
    assertEquals(6, storedVersions.size());
  }

  @Test
  void testDelete() {
    String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version1);

    chartDao.upsert(chart1);
    chartDao.delete(List.of(chart1Name));
    List<Chart> deletedVersions = chartDao.get(true);

    assertEquals(1, deletedVersions.size());
    Chart deletedVersion = deletedVersions.get(0);
    assertNotNull(deletedVersion.inactiveAt());
  }

  @Test
  void testDelete_noNames() {
    final String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    Chart chart1 = new Chart(chart1Name, chart1Version1);

    chartDao.upsert(chart1);
    chartDao.delete(List.of());
    List<Chart> versions = chartDao.get();

    assertNotNull(versions);
    assertEquals(1, versions.size());
    assertEquals(chart1Name, versions.get(0).name());
    assertEquals(chart1Version1, versions.get(0).version());
  }

  @Test
  void testMultiDelete() {
    final String chart1Name = "chart-name-here";
    String chart1Version1 = ChartTestUtils.makeChartVersion(0);
    String chart1Version2 = ChartTestUtils.makeChartVersion(1);
    Chart oldChart1 = new Chart(chart1Name, chart1Version1);
    Chart newChart1 = new Chart(chart1Name, chart1Version2);

    final String chart2Name = "chart-name-here-too";
    String chart2Version1 = ChartTestUtils.makeChartVersion(2);
    String chart2Version2 = ChartTestUtils.makeChartVersion(3);
    Chart oldChart2 = new Chart(chart2Name, chart2Version1);
    Chart newChart2 = new Chart(chart2Name, chart2Version2);

    final String chart3Name = "chart-version-name-again";
    String chart3Version1 = ChartTestUtils.makeChartVersion(4);
    String chart3Version2 = ChartTestUtils.makeChartVersion(5);
    Chart oldChart3 = new Chart(chart3Name, chart3Version1);
    Chart newChart3 = new Chart(chart3Name, chart3Version2);

    chartDao.upsert(oldChart1);
    chartDao.upsert(newChart1);
    chartDao.upsert(oldChart2);
    chartDao.upsert(newChart2);
    chartDao.upsert(oldChart3);
    chartDao.upsert(newChart3);

    chartDao.delete(List.of(chart1Name, chart2Name));

    List<Chart> allVersions = chartDao.get(true);
    for (Chart version : allVersions) {
      switch (version.name()) {
        case chart1Name, chart2Name:
          assertNotNull(version.inactiveAt());
          break;
        case chart3Name:
          if (chart3Version2.equals(version.version())) {
            assertNull(version.inactiveAt());
          } else {
            assertNotNull(version.inactiveAt());
          }
          break;
        default:
          throw new IllegalStateException("unexpected chartName encountered");
      }
    }
  }

  @Nullable
  private static Chart getByChart(List<Chart> storedVersions, String chartVersion) {
    return storedVersions.stream()
        .filter(version -> chartVersion.equals(version.version()))
        .findFirst()
        .orElse(null);
  }
}
