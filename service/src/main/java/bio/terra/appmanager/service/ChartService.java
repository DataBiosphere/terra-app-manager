package bio.terra.appmanager.service;

import bio.terra.appmanager.dao.ChartVersionDao;
import bio.terra.appmanager.model.ChartVersion;
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
}