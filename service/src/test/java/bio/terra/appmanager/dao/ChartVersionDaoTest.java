package bio.terra.appmanager.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import bio.terra.appmanager.model.ChartVersion;
import java.util.Date;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChartVersionDaoTest extends BaseDaoTest {
  @Autowired ChartVersionDao versionDao;

  @AfterEach
  void clearDatabase() {
    versionDao.clearRepository();
  }

  @Test
  void testSingleVersionUpsert() {
    String chartName = "chart-name-here";
    ChartVersion version = createVersion(chartName);

    versionDao.upsert(version);
    List<ChartVersion> storedVersions = versionDao.get(List.of(chartName), false);

    assertEquals(1, storedVersions.size());
    ChartVersion storedVersion = storedVersions.get(0);
    assertEquals(chartName, storedVersion.getChartName());
    assertNotEquals(version.getActiveAt(), storedVersion.getActiveAt());
  }

  @Test
  void testMultiVersionUpsert() {
    String chartName = "chart-name-here";

    String chartVersion1 = "chart-name-here";
    ChartVersion version1 = createVersion(chartName, chartVersion1);

    String chartVersion2 = "chart-version-here-too";
    ChartVersion version2 = createVersion(chartName, chartVersion2);

    versionDao.upsert(version1);
    versionDao.upsert(version2);
    List<ChartVersion> storedVersions = versionDao.get(List.of(chartName), true);

    assertEquals(2, storedVersions.size());
    ChartVersion targetVersion = getByChartVersion(storedVersions, chartVersion1);
    assertNotNull(targetVersion);
    assertEquals(version1.getChartVersion(), targetVersion.getChartVersion());
    assertNotNull(targetVersion.getInactiveAt());

    targetVersion = getByChartVersion(storedVersions, chartVersion2);
    assertNotNull(targetVersion);
    assertEquals(version2.getChartVersion(), targetVersion.getChartVersion());
    assertNull(targetVersion.getInactiveAt());
  }

  @Nullable
  private static ChartVersion getByChartVersion(
      List<ChartVersion> storedVersions, String chartVersion) {
    return storedVersions.stream()
        .filter(version -> chartVersion.equals(version.getChartVersion()))
        .findFirst()
        .orElse(null);
  }

  @Nullable
  private static ChartVersion getByChartName(List<ChartVersion> storedVersions, String chartName) {
    return storedVersions.stream()
        .filter(version -> chartName.equals(version.getChartVersion()))
        .findFirst()
        .orElse(null);
  }

  @Test
  void testMultiNameGet() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = createVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = createVersion(chartName1, chartVersion1_2);

    String chartName2 = "chart-version-here-too";
    String chartVersion2_1 = "chart-version-here-3";
    String chartVersion2_2 = "chart-version-here-four";
    ChartVersion version2_1 = createVersion(chartName2, chartVersion2_1);
    ChartVersion version2_2 = createVersion(chartName2, chartVersion2_2);

    String chartName3 = "chart-version-here-again";
    String chartVersion3_1 = "chart-version-here-5";
    String chartVersion3_2 = "chart-version-here-six";
    ChartVersion version3_1 = createVersion(chartName3, chartVersion3_1);
    ChartVersion version3_2 = createVersion(chartName3, chartVersion3_2);

    versionDao.upsert(version1_1);
    versionDao.upsert(version1_2);
    versionDao.upsert(version2_1);
    versionDao.upsert(version2_2);
    versionDao.upsert(version3_1);
    versionDao.upsert(version3_2);

    List<ChartVersion> storedVersions = versionDao.get((List.of(chartName1, chartName2)), false);
    assertEquals(2, storedVersions.size());

    List<ChartVersion> targetCharts =
        storedVersions.stream()
            .filter(version -> chartName1.equals(version.getChartName()))
            .toList();
    assertEquals(1, targetCharts.size());
    ChartVersion targetVersion = targetCharts.get(0);
    assertEquals(chartVersion1_2, targetVersion.getChartVersion());

    targetCharts =
        storedVersions.stream()
            .filter(version -> chartName2.equals(version.getChartName()))
            .toList();
    assertEquals(1, targetCharts.size());
    targetVersion = targetCharts.get(0);
    assertEquals(chartVersion2_2, targetVersion.getChartVersion());
  }

  @Test
  void testGetAll() {
    String chartName1 = "chart-name-here";
    String chartVersion1_1 = "chart-version-here-1";
    String chartVersion1_2 = "chart-version-here-too";
    ChartVersion version1_1 = createVersion(chartName1, chartVersion1_1);
    ChartVersion version1_2 = createVersion(chartName1, chartVersion1_2);

    String chartName2 = "chart-version-here-too";
    String chartVersion2_1 = "chart-version-here-3";
    String chartVersion2_2 = "chart-version-here-four";
    ChartVersion version2_1 = createVersion(chartName2, chartVersion2_1);
    ChartVersion version2_2 = createVersion(chartName2, chartVersion2_2);

    String chartName3 = "chart-version-here-again";
    String chartVersion3_1 = "chart-version-here-5";
    String chartVersion3_2 = "chart-version-here-six";
    ChartVersion version3_1 = createVersion(chartName3, chartVersion3_1);
    ChartVersion version3_2 = createVersion(chartName3, chartVersion3_2);

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
  void testDelete() {}

  @Test
  void testMultiDelete() {}

  private ChartVersion createVersion(String chartName) {
    return createVersion(chartName, null, null, null, null);
  }

  private ChartVersion createVersion(String chartName, String chartVersion) {
    return createVersion(chartName, chartVersion, null, null, null);
  }

  private ChartVersion createVersion(
      String chartName, String chartVersion, String appVersion, Date activeAt, Date inactiveAt) {
    ChartVersion version = new ChartVersion();
    version.setChartName(chartName);
    version.setChartVersion(chartVersion);
    version.setAppVersion(appVersion);
    version.setActiveAt(activeAt);
    version.setInactiveAt(inactiveAt);
    return version;
  }
}
