package bio.terra.appmanager.model;

import bio.terra.common.exception.InconsistentFieldsException;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.regex.Pattern;

public record ChartVersion(
    String chartName,
    String chartVersion,
    @Nullable String appVersion,
    @Nullable Date activeAt,
    @Nullable Date inactiveAt) {

  public ChartVersion {
    if (!isChartNameValid(chartName)) {
      throw new InconsistentFieldsException(
          "Chart name "
              + chartName
              + " is invalid, must follow chart name conventions: https://helm.sh/docs/chart_best_practices/conventions/#chart-names. Regex used: "
              + chartNameRegex);
    }
    if (!isChartVersionValid(chartVersion)) {
      throw new InconsistentFieldsException(
          "Chart version "
              + chartVersion
              + " is invalid, must follow chart version conventions: https://helm.sh/docs/chart_best_practices/values/. Value must be camel case, the first letter must be lowercase and value must have letters only. Regex used: '^[a-z][a-z]*(([A-Z][a-z]+)*[A-Z]?|([a-z]+[A-Z])*|[A-Z])$'");
    }
  }

  // Must follow chart name conventions:
  // https://helm.sh/docs/chart_best_practices/conventions/#chart-names
  // Alphanumeric lowercase with dashes allowed, additionally we impose 1-25 character limit
  static final String chartNameRegex = "^[a-z0-9-]{1,25}$";
  // Must follow chart value conventions:
  // https://helm.sh/docs/chart_best_practices/values/#naming-conventions
  // Camel case, requiring the first letter to be lowercase with no numeric characters. Letters
  // only. Additionally we impose 1-25 character limit
  // See regex test cases: https://regex101.com/r/4h7A1I/8
  static final String chartValueRegex = "^[a-z]+(([A-Z][a-z]+)*[A-Z]?|([a-z]+[A-Z])*|[A-Z])$";

  public static boolean isChartNameValid(String chartName) {
    Pattern chartNamePattern = Pattern.compile(chartNameRegex);
    return chartNamePattern.matcher(chartName).matches();
  }

  public static boolean isChartVersionValid(String chartVersion) {
    Pattern chartVersionPattern = Pattern.compile(chartValueRegex);
    return chartVersionPattern.matcher(chartVersion).matches() && chartVersion.length() < 25;
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
