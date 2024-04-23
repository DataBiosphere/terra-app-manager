package bio.terra.appmanager.model;

import java.util.Date;
import java.util.Objects;

public record ChartVersion(
    String chartName, String chartVersion, String appVersion, Date activeAt, Date inactiveAt) {
  public ChartVersion {
    Objects.requireNonNull(chartName);
    Objects.requireNonNull(chartVersion);
  }

  public ChartVersion(String chartName, String chartVersion) {
    this(chartName, chartVersion, null, null, null);
  }

  public ChartVersion activate(Date activeAt) {
    return new ChartVersion(chartName(), chartVersion(), appVersion(), activeAt, null);
  }

  public ChartVersion inactivate(Date inactiveAt) {
    return new ChartVersion(chartName(), chartVersion(), appVersion(), activeAt(), inactiveAt);
  }
}
