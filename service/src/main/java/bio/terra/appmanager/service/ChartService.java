package bio.terra.appmanager.service;

import bio.terra.appmanager.dao.ChartDao;
import bio.terra.appmanager.model.Chart;
import bio.terra.common.db.ReadTransaction;
import bio.terra.common.db.WriteTransaction;
import jakarta.validation.constraints.NotNull;
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
