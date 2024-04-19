package bio.terra.appmanager.dao;

import bio.terra.appmanager.model.ChartVersion;
import com.google.common.annotations.VisibleForTesting;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.springframework.stereotype.Repository;

/**
 * The ChartVersion repository is based on the following assumptions: - there should be only ever
 * one active {@link ChartVersion} - {@link ChartVersion#activeAt()} and {@link
 * ChartVersion#inactiveAt()} map to created_at and deleted_at respectively -
 *
 * <p>There was a decision in a PR to keep this "dumb" as to managing the semantics.
 */
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
  public void create(ChartVersion version) {
    inmemStore.computeIfAbsent(version.chartName(), chartName -> new Stack<>());
    Stack<ChartVersion> chartVersions = inmemStore.get(version.chartName());
    chartVersions.push(version);
  }

  /**
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  public List<ChartVersion> get(boolean includeAll) {
    return get(List.of(), includeAll);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @return list of ACTIVE {@link ChartVersion}s based on the parameters provided
   */
  public List<ChartVersion> get(List<String> chartNames) {
    return get(chartNames, false);
  }

  /**
   * @param chartNames non-null list of chartNames to filter the return results.
   * @param includeAll {@code true} if we should return all versions, including inactive {@link
   *     ChartVersion}s.
   * @return list of {@link ChartVersion}s based on the parameters provided
   */
  @WithSpan
  public List<ChartVersion> get(@NotNull List<String> chartNames, boolean includeAll) {
    return inmemStore.entrySet().stream()
        .filter(entry -> chartNames.isEmpty() || chartNames.contains(entry.getKey()))
        .map(entry -> (includeAll) ? (entry.getValue()) : (List.of(entry.getValue().peek())))
        .flatMap(List::stream)
        .toList();
  }

  /**
   * Soft-delete all {@link ChartVersion}s for the {@code chartNames} provided
   *
   * @param chartNames list of chart names to delete.
   */
  @WithSpan
  public void delete(List<String> chartNames) {
    delete(chartNames, new Date());
  }

  public void delete(List<String> chartNames, Date now) {
    inmemStore.forEach(
        (chartName, chartVersions) -> {
          if (chartNames.contains(chartName)) {
            inactivateExistingVersion(chartVersions, now);
          }
        });
  }

  private static void inactivateExistingVersion(
      Stack<ChartVersion> chartVersions, Date inactiveDate) {
    if (chartVersions.empty()) {
      return; // nothing to invalidate
    }
    ChartVersion currentVersion = chartVersions.pop();
    ChartVersion inactiveVersion = currentVersion.inactivate(inactiveDate);
    chartVersions.push(inactiveVersion);
  }
}
