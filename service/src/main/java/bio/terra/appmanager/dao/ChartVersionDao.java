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
    inmemStore.computeIfAbsent(version.chartName(), chartName -> new Stack<>());
    Stack<ChartVersion> chartVersions = inmemStore.get(version.chartName());

    // make sure the activeDate and inactiveDate(s) are the same date/time
    Date currentDate = new Date();
    deleteActiveVersions(chartVersions, currentDate);

    chartVersions.push(version.activate(currentDate));
  }

  /**
   * @return list of ACTIVE {@link ChartVersion}s
   */
  public List<ChartVersion> get() {
    return get(false);
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
    // keep all date/times the same re: transaction
    Date currentDate = new Date();

    inmemStore.entrySet().stream()
        .filter(entry -> chartNames.isEmpty() || chartNames.contains(entry.getKey()))
        .forEach(entry -> deleteActiveVersions(entry.getValue(), currentDate));
  }

  private static void deleteActiveVersions(Stack<ChartVersion> chartVersions, Date inactiveDate) {
    if (chartVersions.empty()) {
      return; // nothing to invalidate
    }
    ChartVersion currentVersion = chartVersions.pop();
    ChartVersion inactiveVersion = currentVersion.inactivate(inactiveDate);
    chartVersions.push(inactiveVersion);
  }
}
