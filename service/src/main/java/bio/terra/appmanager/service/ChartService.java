package bio.terra.appmanager.service;

import bio.terra.appmanager.dao.ChartVersionDao;
import bio.terra.appmanager.model.ChartVersion;
import bio.terra.common.db.ReadTransaction;
import bio.terra.common.db.WriteTransaction;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChartService {

  private final ChartVersionDao chartVersionDao;

  public ChartService(ChartVersionDao chartVersionDao) {
    this.chartVersionDao = chartVersionDao;
  }

  /**
   * Create chart entries with associated chart and application versions.
   *
   * @param versions non-null list of {@ ChartVersion}s to create
   */
  @WriteTransaction
  public void createVersions(@NotNull List<ChartVersion> versions) {
    versions.forEach(chartVersionDao::upsert);
  }

  /**
   * Soft-delete the specified chart entries with associated chartName.
   *
   * @param names non-null list of {@ ChartName}s to delete
   */
  public void deleteVersions(@NotNull List<String> names) {
    chartVersionDao.delete(names);
  }

  /**
   * Get chart versions by name
   *
   * @param names The names of charts to retrieve the versions of
   * @param includeAll Whether to include inactive versions of that chart
   * @return A list of chart versions specified
   */
  @ReadTransaction
  public List<ChartVersion> getVersions(@NotNull List<String> names, @NotNull Boolean includeAll) {
    return chartVersionDao.get(names, includeAll);
  }
}
