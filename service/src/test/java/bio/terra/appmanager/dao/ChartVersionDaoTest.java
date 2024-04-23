package bio.terra.appmanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import bio.terra.appmanager.model.ChartVersion;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ChartVersionDaoTest extends BaseDaoTest {
  @Autowired ChartVersionDao versionDao;

  @AfterEach
  void clearDatabase() {
    versionDao.clearRepository();
  }

  @Test
  void testSingleVersionUpsert() {
    String chartName = "chart-name-here";
    String chartVersion = "chart-version-here";
    ChartVersion version = new ChartVersion(chartName, chartVersion);

    versionDao.upsert(version);
    List<ChartVersion> storedVersions = versionDao.get(List.of(chartName), false);

    assertEquals(1, storedVersions.size());
    ChartVersion storedVersion = storedVersions.get(0);
    assertEquals(chartName, storedVersion.chartName());
    assertNotEquals(version.activeAt(), storedVersion.activeAt());
  }

  @Test
  void testMultiVersionUpsert() {
    String chartName = "chart-name-here";

    String chartVersion1 = "chart-name-here";
    ChartVersion version1 = new ChartVersion(chartName, chartVersion1);

    String chartVersion2 = "chart-version-here-too";
    ChartVersion version2 = new ChartVersion(chartName, chartVersion2);

    versionDao.upsert(version1);
    versionDao.upsert(version2);
    List<ChartVersion> storedVersions = versionDao.get(List.of(chartName), true);

    assertEquals(2, storedVersions.size());
    ChartVersion targetVersion = getByChartVersion(storedVersions, chartVersion1);
    assertNotNull(targetVersion);
    assertEquals(version1.chartVersion(), targetVersion.chartVersion());
    assertNotNull(targetVersion.inactiveAt());

    targetVersion = getByChartVersion(storedVersions, chartVersion2);
    assertNotNull(targetVersion);
    assertEquals(version2.chartVersion(), targetVersion.chartVersion());
    assertNull(targetVersion.inactiveAt());
  }

  @Test
  void testMultiNameGet() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = new ChartVersion(chartName1, chartVersion1_2);

    String chartName2 = "chart-version-here-too";
    String chartVersion2_1 = "chart-version-here-3";
    String chartVersion2_2 = "chart-version-here-four";
    ChartVersion version2_1 = new ChartVersion(chartName2, chartVersion2_1);
    ChartVersion version2_2 = new ChartVersion(chartName2, chartVersion2_2);

    String chartName3 = "chart-version-here-again";
    String chartVersion3_1 = "chart-version-here-5";
    String chartVersion3_2 = "chart-version-here-six";
    ChartVersion version3_1 = new ChartVersion(chartName3, chartVersion3_1);
    ChartVersion version3_2 = new ChartVersion(chartName3, chartVersion3_2);

    versionDao.upsert(version1_1);
    versionDao.upsert(version1_2);
    versionDao.upsert(version2_1);
    versionDao.upsert(version2_2);
    versionDao.upsert(version3_1);
    versionDao.upsert(version3_2);

    List<ChartVersion> storedVersions = versionDao.get((List.of(chartName1, chartName2)));
    assertEquals(2, storedVersions.size());

    List<ChartVersion> targetCharts =
        storedVersions.stream().filter(version -> chartName1.equals(version.chartName())).toList();
    assertEquals(1, targetCharts.size());
    ChartVersion targetVersion = targetCharts.get(0);
    assertEquals(chartVersion1_2, targetVersion.chartVersion());

    targetCharts =
        storedVersions.stream().filter(version -> chartName2.equals(version.chartName())).toList();
    assertEquals(1, targetCharts.size());
    targetVersion = targetCharts.get(0);
    assertEquals(chartVersion2_2, targetVersion.chartVersion());
  }

  @Test
  void testGetAll() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = new ChartVersion(chartName1, chartVersion1_2);

    String chartName2 = "chart-version-here-too";
    String chartVersion2_1 = "chart-version-here-3";
    String chartVersion2_2 = "chart-version-here-four";
    ChartVersion version2_1 = new ChartVersion(chartName2, chartVersion2_1);
    ChartVersion version2_2 = new ChartVersion(chartName2, chartVersion2_2);

    String chartName3 = "chart-version-here-again";
    String chartVersion3_1 = "chart-version-here-5";
    String chartVersion3_2 = "chart-version-here-six";
    ChartVersion version3_1 = new ChartVersion(chartName3, chartVersion3_1);
    ChartVersion version3_2 = new ChartVersion(chartName3, chartVersion3_2);

    versionDao.upsert(version1_1);
    versionDao.upsert(version1_2);
    versionDao.upsert(version2_1);
    versionDao.upsert(version2_2);
    versionDao.upsert(version3_1);
    versionDao.upsert(version3_2);

    List<ChartVersion> storedVersions = versionDao.get(true);
    assertEquals(6, storedVersions.size());
  }

  @Test
  void testDelete() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);

    versionDao.upsert(version1_1);
    versionDao.delete(List.of(chartName1));
    List<ChartVersion> deletedVersions = versionDao.get(true);

    assertEquals(1, deletedVersions.size());
    ChartVersion deletedVersion = deletedVersions.get(0);
    assertNotNull(deletedVersion.inactiveAt());
  }

  @Test
  void testMultiDelete() {
    final String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = new ChartVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = new ChartVersion(chartName1, chartVersion1_2);

    final String chartName2 = "chart-version-here-too";
    String chartVersion2_1 = "chart-version-here-3";
    String chartVersion2_2 = "chart-version-here-four";
    ChartVersion version2_1 = new ChartVersion(chartName2, chartVersion2_1);
    ChartVersion version2_2 = new ChartVersion(chartName2, chartVersion2_2);

    final String chartName3 = "chart-version-here-again";
    String chartVersion3_1 = "chart-version-here-5";
    String chartVersion3_2 = "chart-version-here-six";
    ChartVersion version3_1 = new ChartVersion(chartName3, chartVersion3_1);
    ChartVersion version3_2 = new ChartVersion(chartName3, chartVersion3_2);

    versionDao.upsert(version1_1);
    versionDao.upsert(version1_2);
    versionDao.upsert(version2_1);
    versionDao.upsert(version2_2);
    versionDao.upsert(version3_1);
    versionDao.upsert(version3_2);

    versionDao.delete(List.of(chartName1, chartName2));

    List<ChartVersion> allVersions = versionDao.get(true);
    for (ChartVersion version : allVersions) {
      switch (version.chartName()) {
        case chartName1:
        case chartName2:
          assertNotNull(version.inactiveAt());
          break;
        case chartName3:
          if (chartVersion3_2.equals(version.chartVersion())) {
            assertNull(version.inactiveAt());
          } else {
            assertNotNull(version.inactiveAt());
          }
      }
    }
  }

  @Nullable
  private static ChartVersion getByChartVersion(
      List<ChartVersion> storedVersions, String chartVersion) {
    return storedVersions.stream()
        .filter(version -> chartVersion.equals(version.chartVersion()))
        .findFirst()
        .orElse(null);
  }

  @Nullable
  private static ChartVersion getByChartName(List<ChartVersion> storedVersions, String chartName) {
    return storedVersions.stream()
        .filter(version -> chartName.equals(version.chartVersion()))
        .findFirst()
        .orElse(null);
  }
}
