package bio.terra.appmanager.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Date;
import java.util.Objects;

public record ChartVersion(
    // https://helm.sh/docs/chart_best_practices/conventions/#chart-names
    @NotBlank
        @Pattern(
            regexp = "^[a-z0-9-]{1,25}$",
            message =
                "Chart name is invalid, must follow chart name conventions: https://helm.sh/docs/chart_best_practices/conventions/#chart-names. Regex used: '^[a-z0-9-]{1,25}$'")
        String chartName,

    // https://helm.sh/docs/chart_best_practices/values/#naming-conventions
    // See regex test cases: https://regex101.com/r/4h7A1I/8
    @NotBlank
        @Pattern(
            regexp = "^[a-z][a-z]*(([A-Z][a-z]+)*[A-Z]?|([a-z]+[A-Z])*|[A-Z])$",
            message =
                "Chart version is invalid, must follow chart name conventions: https://helm.sh/docs/chart_best_practices/values/#naming-conventions. Chart value must be camel case with no numbers and not begin with an uppercase letter.")
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
