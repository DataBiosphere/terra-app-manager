package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.AdminApi;
import bio.terra.appmanager.api.model.Chart;
import bio.terra.appmanager.api.model.ChartArray;
import bio.terra.appmanager.service.ChartService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * Please see the ./DESIGN.md document (located in the repo-root) for more information about how
 * requests are authenticated and security is enforced.
 *
 * @see AdminControllerInterceptor
 * @see AdminControllerInterceptorConfigurer
 */
@Controller
public class AdminController implements AdminApi {
  private final ChartService chartService;

  public AdminController(ChartService chartService) {
    this.chartService = chartService;
  }

  @Override
  public ResponseEntity<Void> createCharts(List<Chart> body) {
    List<bio.terra.appmanager.model.Chart> charts = List.of();
    try {
      charts = body.stream().map((bio.terra.appmanager.model.Chart::fromApi)).toList();
    } catch (NullPointerException npe) {
      return ResponseEntity.badRequest().build();
    }

    this.chartService.createCharts(charts);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> updateChart(List<Chart> body) {
    List<bio.terra.appmanager.model.Chart> versions = List.of();
    try {
      versions = body.stream().map((bio.terra.appmanager.model.Chart::fromApi)).toList();
    } catch (NullPointerException npe) {
      return ResponseEntity.badRequest().build();
    }

    this.chartService.updateVersions(versions);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteChart(String body) {
    this.chartService.deleteVersion(body);
    return ResponseEntity.noContent().build();
  }

  /**
   * @param chartName optional name of chart to return values for, otherwise return all charts
   * @param includeAll non-null indicator to include deleted charts as well as active
   * @return {@link ChartArray} of charts matching input criteria
   */
  @Override
  public ResponseEntity<ChartArray> getCharts(String chartName, Boolean includeAll) {
    List<String> versions = chartName == null ? List.of() : List.of(chartName);
    List<bio.terra.appmanager.model.Chart> dbResult =
        this.chartService.getCharts(versions, includeAll);

    List<bio.terra.appmanager.api.model.Chart> apiCharts =
        dbResult.stream().map(bio.terra.appmanager.model.Chart::toApi).toList();

    ChartArray apiResult = new ChartArray();
    apiResult.addAll(apiCharts);

    return ResponseEntity.ok(apiResult);
  }
}
