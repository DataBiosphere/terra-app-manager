package bio.terra.appmanager.model;

import bio.terra.common.exception.InconsistentFieldsException;
import jakarta.annotation.Nullable;
import java.util.Date;
import java.util.regex.Pattern;

public record Chart(
    String name,
    String version,
    @Nullable String appVersion,
    @Nullable Date activeAt,
    @Nullable Date inactiveAt) {

  public Chart {
    if (!isChartNameValid(name)) {
      throw new InconsistentFieldsException(
          "Chart name "
              + name
              + " is invalid, must follow chart name conventions: https://helm.sh/docs/chart_best_practices/conventions/#chart-names. Regex used: "
              + chartNameRegex);
    }
    if (!isChartVersionValid(version)) {
      throw new InconsistentFieldsException(
          "Chart version "
              + version
              + " is invalid, must follow chart version conventions: https://helm.sh/docs/chart_best_practices/values/. Value must be camel case, the first letter must be lowercase and value must have letters only. Regex used: "
              + chartValueRegex);
    }
  }

  // Must follow chart name conventions:
  // https://helm.sh/docs/chart_best_practices/conventions/#chart-names
  // Alphanumeric lowercase with dashes allowed, additionally we impose 1-25 character limit
  static final String chartNameRegex = "^[a-z0-9-]{1,25}$";
  static final Pattern chartNamePattern = Pattern.compile(chartNameRegex);
  // Must follow chart value conventions:
  // https://helm.sh/docs/chart_best_practices/values/#naming-conventions
  // Camel case, requiring the first letter to be lowercase with no numeric characters. Letters
  // only. Additionally we impose 1-25 character limit
  // See regex test cases: https://regex101.com/r/nz2Ccj/1
  static final String chartValueRegex = "^[a-z]+(([A-Z][a-z]+)*[A-Z]?)$";
  static final Pattern chartVersionPattern = Pattern.compile(chartValueRegex);

  public static boolean isChartNameValid(String chartName) {
    return chartNamePattern.matcher(chartName).matches();
  }

  public static boolean isChartVersionValid(String chartVersion) {
    return chartVersionPattern.matcher(chartVersion).matches() && chartVersion.length() < 25;
  }

  public Chart(String chartName, String chartVersion) {
    this(chartName, chartVersion, null, null, null);
  }

  public static Chart fromApi(bio.terra.appmanager.api.model.Chart source) {
    return new Chart(
        source.getName(),
        source.getVersion(),
        source.getAppVersion(),
        source.getActiveAt(),
        source.getInactiveAt());
  }

  public bio.terra.appmanager.api.model.Chart toApi() {
    return new bio.terra.appmanager.api.model.Chart()
        .name(this.name)
        .version(this.version)
        .appVersion(this.appVersion)
        .activeAt(this.activeAt)
        .inactiveAt(this.inactiveAt);
  }

  public Chart activate(Date activeAt) {
    return new Chart(name(), version(), appVersion(), activeAt, null);
  }

  public Chart inactivate(Date inactiveAt) {
    return new Chart(name(), version(), appVersion(), activeAt(), inactiveAt);
  }
}
