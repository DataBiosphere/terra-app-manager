package bio.terra.appmanager.service;

import bio.terra.appmanager.controller.ChartNotFoundException;
import bio.terra.appmanager.dao.ChartDao;
import bio.terra.appmanager.model.Chart;
import bio.terra.common.db.ReadTransaction;
import bio.terra.common.db.WriteTransaction;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChartService {

  private final ChartDao chartDao;

  public ChartService(ChartDao chartDao) {
    this.chartDao = chartDao;
  }

  /**
   * Create chart entries with associated chart and application versions.
   *
   * @param charts non-null list of {@link Chart}s to create
   */
  @WriteTransaction
  public void createCharts(@NotNull List<Chart> charts) {
    charts.forEach(chartDao::upsert);
  }

  /**
   * Update chart entries with associated chart and application versions. It is assumed that the
   * caller of this validates whether the versions exist, and this method `upserts` for all records
   *
   * @param versions non-null list of {@link Chart}s to update
   */
  @WriteTransaction
  public void updateVersions(@NotNull List<Chart> versions) {

    List<Chart> existingVersions;
    ArrayList<String> nonexistentVersions = new ArrayList<>();
    for (Chart chart : versions) {
      existingVersions = getCharts(List.of(chart.name()), true);
      if (existingVersions.isEmpty()) {
        nonexistentVersions.add(chart.name());
      }
    }

    if (!nonexistentVersions.isEmpty()) {
      throw new ChartNotFoundException(
          "The chart(s) you attempted to update do not currently exist, please create first: "
              + nonexistentVersions);
    }

    versions.forEach(chartDao::upsert);
  }

  /**
   * Soft-delete the specified chart entries with associated name.
   *
   * @param name non-null chart name to delete
   */
  public void deleteVersion(@NotNull String name) {
    chartDao.delete(List.of(name));
  }

  /**
   * Get chart versions by name
   *
   * @param names The names of charts to retrieve the versions of
   * @param includeAll Whether to include inactive versions of that chart
   * @return A list of chart versions specified
   */
  @ReadTransaction
  public List<Chart> getCharts(@NotNull List<String> names, @NotNull Boolean includeAll) {
    return chartDao.get(names, includeAll);
  }
}
