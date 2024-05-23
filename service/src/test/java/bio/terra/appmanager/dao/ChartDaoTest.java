package bio.terra.appmanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import bio.terra.appmanager.model.Chart;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChartDaoTest extends BaseDaoTest {
  @Autowired ChartDao chartDao;

  @Test
  void testSingleVersionUpsert() {
    String chartName = "chart-name-here";
    String chartVersion = "chartVersionHere";
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

    String chartVersion1 = "chartVersionHere";
    Chart version1 = new Chart(chartName, chartVersion1);

    String chartVersion2 = "chartVersionHereToo";
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
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHereOne";
    String chartVersion1_2 = "chartVersionHereToo";
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);
    Chart version1_2 = new Chart(chartName1, chartVersion1_2);

    String chartName2 = "chart-name-here-too";
    String chartVersion2_1 = "chartVersionHereThree";
    String chartVersion2_2 = "chartVersionHereFour";
    Chart version2_1 = new Chart(chartName2, chartVersion2_1);
    Chart version2_2 = new Chart(chartName2, chartVersion2_2);

    String chartName3 = "chart-name";
    String chartVersion3_1 = "chartVersionHereFive";
    String chartVersion3_2 = "chartVersionHereSix";
    Chart version3_1 = new Chart(chartName3, chartVersion3_1);
    Chart version3_2 = new Chart(chartName3, chartVersion3_2);

    chartDao.upsert(version1_1);
    chartDao.upsert(version1_2);
    chartDao.upsert(version2_1);
    chartDao.upsert(version2_2);
    chartDao.upsert(version3_1);
    chartDao.upsert(version3_2);

    List<Chart> storedVersions = chartDao.get((List.of(chartName1, chartName2)));
    assertEquals(2, storedVersions.size());

    List<Chart> targetCharts =
        storedVersions.stream().filter(version -> chartName1.equals(version.name())).toList();
    assertEquals(1, targetCharts.size());
    Chart targetVersion = targetCharts.get(0);
    assertEquals(chartVersion1_2, targetVersion.version());

    targetCharts =
        storedVersions.stream().filter(version -> chartName2.equals(version.name())).toList();
    assertEquals(1, targetCharts.size());
    targetVersion = targetCharts.get(0);
    assertEquals(chartVersion2_2, targetVersion.version());
  }

  @Test
  void testGetAll() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHereOne";
    String chartVersion1_2 = "chartVersionHereToo";
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);
    Chart version1_2 = new Chart(chartName1, chartVersion1_2);

    String chartName2 = "chart-name-here-too";
    String chartVersion2_1 = "chartVersionHereThree";
    String chartVersion2_2 = "chartVersionHereFour";
    Chart version2_1 = new Chart(chartName2, chartVersion2_1);
    Chart version2_2 = new Chart(chartName2, chartVersion2_2);

    String chartName3 = "chart-name-here-again";
    String chartVersion3_1 = "chartVersionHereFive";
    String chartVersion3_2 = "chartVersionHereSix";
    Chart version3_1 = new Chart(chartName3, chartVersion3_1);
    Chart version3_2 = new Chart(chartName3, chartVersion3_2);

    chartDao.upsert(version1_1);
    chartDao.upsert(version1_2);
    chartDao.upsert(version2_1);
    chartDao.upsert(version2_2);
    chartDao.upsert(version3_1);
    chartDao.upsert(version3_2);

    List<Chart> storedVersions = chartDao.get(true);
    assertEquals(6, storedVersions.size());
  }

  @Test
  void testDelete() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHere";
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);

    chartDao.upsert(version1_1);
    chartDao.delete(List.of(chartName1));
    List<Chart> deletedVersions = chartDao.get(true);

    assertEquals(1, deletedVersions.size());
    Chart deletedVersion = deletedVersions.get(0);
    assertNotNull(deletedVersion.inactiveAt());
  }

  @Test
  void testDelete_noNames() {
    final String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHere";
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);

    chartDao.upsert(version1_1);
    chartDao.delete(List.of());
    List<Chart> versions = chartDao.get();

    assertNotNull(versions);
    assertEquals(1, versions.size());
    assertEquals(chartName1, versions.get(0).name());
    assertEquals(chartVersion1_1, versions.get(0).version());
  }

  @Test
  void testMultiDelete() {
    final String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chartVersionHere";
    String chartVersion1_2 = "chartVersionHereToo";
    Chart version1_1 = new Chart(chartName1, chartVersion1_1);
    Chart version1_2 = new Chart(chartName1, chartVersion1_2);

    final String chartName2 = "chart-name-here-too";
    String chartVersion2_1 = "chartVersionHereThree";
    String chartVersion2_2 = "chartVersionHereFour";
    Chart version2_1 = new Chart(chartName2, chartVersion2_1);
    Chart version2_2 = new Chart(chartName2, chartVersion2_2);

    final String chartName3 = "chart-version-name-again";
    String chartVersion3_1 = "chartVersionHereFive";
    String chartVersion3_2 = "chartVersionHereSix";
    Chart version3_1 = new Chart(chartName3, chartVersion3_1);
    Chart version3_2 = new Chart(chartName3, chartVersion3_2);

    chartDao.upsert(version1_1);
    chartDao.upsert(version1_2);
    chartDao.upsert(version2_1);
    chartDao.upsert(version2_2);
    chartDao.upsert(version3_1);
    chartDao.upsert(version3_2);

    chartDao.delete(List.of(chartName1, chartName2));

    List<Chart> allVersions = chartDao.get(true);
    for (Chart version : allVersions) {
      switch (version.name()) {
        case chartName1:
        case chartName2:
          assertNotNull(version.inactiveAt());
          break;
        case chartName3:
          if (chartVersion3_2.equals(version.version())) {
            assertNull(version.inactiveAt());
          } else {
            assertNotNull(version.inactiveAt());
          }
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

  @Nullable
  private static Chart getByChartName(List<Chart> storedVersions, String chartName) {
    return storedVersions.stream()
        .filter(version -> chartName.equals(version.version()))
        .findFirst()
        .orElse(null);
  }
}
