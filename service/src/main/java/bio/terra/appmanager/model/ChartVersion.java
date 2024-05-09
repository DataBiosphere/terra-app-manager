package bio.terra.appmanager.model;

import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.Objects;

public record ChartVersion(
    String chartName,
    String chartVersion,
    @Nullable String appVersion,
    @Nullable Date activeAt,
    @Nullable Date inactiveAt) {
  public ChartVersion {
    Objects.requireNonNull(chartName);
    Objects.requireNonNull(chartVersion);
  }

  public ChartVersion(String chartName, String chartVersion) {
    this(chartName, chartVersion, null, null, null);
  }

  public static ChartVersion fromApi(bio.terra.appmanager.api.model.ChartVersion source) {
    return new ChartVersion(
        source.getChartName(),
        source.getChartVersion(),
        source.getAppVersion(),
        source.getActiveAt(),
        source.getInactiveAt());
  }

  public bio.terra.appmanager.api.model.ChartVersion toApi() {
    return new bio.terra.appmanager.api.model.ChartVersion()
        .chartName(this.chartName)
        .chartVersion(this.chartVersion)
        .appVersion(this.appVersion)
        .activeAt(this.activeAt)
        .inactiveAt(this.inactiveAt);
  }

  public ChartVersion activate(Date activeAt) {
    return new ChartVersion(chartName(), chartVersion(), appVersion(), activeAt, null);
  }

  public ChartVersion inactivate(Date inactiveAt) {
    return new ChartVersion(chartName(), chartVersion(), appVersion(), activeAt(), inactiveAt);
  }
}
