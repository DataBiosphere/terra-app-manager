package bio.terra.appmanager.service;

import bio.terra.appmanager.dao.ChartVersionDao;
import bio.terra.appmanager.model.ChartVersion;
import bio.terra.common.db.WriteTransaction;
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
   * @param versions non-null list of <tt>ChartVersion</tt>s
   */
  @WriteTransaction
  public void createVersion(List<ChartVersion> versions) {
    if (versions == null || versions.size() < 1) {
      // TODO: handle the error condition here
    }
    versions.forEach((version) -> chartVersionDao.upsert(version));
    chartVersionDao
        .get(true)
        .forEach(
            version -> {
              logger.info(version.toString());
            });
  }
}
