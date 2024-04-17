package bio.terra.appmanager.dao;

import bio.terra.appmanager.model.ChartVersion;
import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.springframework.stereotype.Repository;

@Repository
public class ChartVersionDao {

  private final Map<String, Stack<ChartVersion>> inmemStore;

  public ChartVersionDao() {
    this.inmemStore = new HashMap<>();
  }

  @VisibleForTesting
  void clearRepository() {
    inmemStore.clear();
  }

  /**
   * @param version {@link ChartVersion} to add to the repository
   */
  @WithSpan
  public void upsert(ChartVersion version) {
    inmemStore.computeIfAbsent(version.getChartName(), chartName -> new Stack<>());
    Stack<ChartVersion> chartVersions = inmemStore.get(version.getChartName());

    // make sure the activeDate and inactiveDate(s) are the same date/time
    Date currentDate = new Date();
    inactivateExistingVersions(version.getChartName(), chartVersions, currentDate);

    // we are doing this to not have unintended side effect - like storing in a database
    ChartVersion localVersion = cloneChartVersion(version);
    localVersion.setActiveAt(currentDate);
    chartVersions.push(localVersion);
  }

  /**
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  public List<ChartVersion> get(boolean includeAll) {
    return get(null, includeAll);
  }

  /**
   * @param chartNames list of chartNames to filter the return results.
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  @WithSpan
  public List<ChartVersion> get(List<String> chartNames, boolean includeAll) {
    List<ChartVersion> chartVersions = new ArrayList<>();
    for (Map.Entry<String, Stack<ChartVersion>> entry : inmemStore.entrySet()) {
      if (chartNames != null && !chartNames.isEmpty() && !chartNames.contains(entry.getKey())) {
        continue; // skip this chartVersion if none supplied
      }
      if (includeAll) {
        chartVersions.addAll(entry.getValue());
      } else {
        chartVersions.add(entry.getValue().peek());
      }
    }
    return chartVersions;
  }

  /**
   * Soft-delete all {@link ChartVersion}s for the {@code chartNames} provided
   *
   * @param chartNames list of chart names to delete.
   */
  @WithSpan
  public void delete(List<String> chartNames) {
    // keep all date/times the same re: transaction
    Date currentDate = new Date();
    inmemStore.forEach(
        (chartName, chartVersions) -> {
          if (chartNames.contains(chartName)) {
            inactivateExistingVersions(chartName, chartVersions, currentDate);
          }
        });
  }

  private static ChartVersion cloneChartVersion(ChartVersion source) {
    ChartVersion target = new ChartVersion();
    target.setChartName(source.getChartName());
    target.setChartVersion(source.getChartVersion());
    target.setAppVersion(source.getAppVersion());
    target.setActiveAt(source.getActiveAt());
    target.setInactiveAt(source.getInactiveAt());
    return target;
  }

  private static void inactivateExistingVersions(
      String chartName, Stack<ChartVersion> chartVersions, Date inactiveDate) {
    chartVersions.forEach(
        version -> {
          if (version.getChartName().equals(chartName) && version.getInactiveAt() == null) {
            version.setInactiveAt(inactiveDate);
          }
        });
  }
}
