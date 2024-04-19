package bio.terra.appmanager.service;

import bio.terra.appmanager.dao.ChartVersionDao;
import bio.terra.appmanager.model.ChartVersion;
import bio.terra.common.db.WriteTransaction;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ChartService {
  private static final Logger logger = LoggerFactory.getLogger(ChartService.class);

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
    versions.forEach(
        version -> {
          createVersion(version, new Date());
        });
  }

  @WriteTransaction
  void createVersion(ChartVersion version, Date now) {
    inactivate(version.chartName(), now);
    chartVersionDao.create(version.activate(now));
  }

  @WriteTransaction
  void inactivate(String chartName, Date now) {
    chartVersionDao.delete(List.of(chartName), now);
  }
}
