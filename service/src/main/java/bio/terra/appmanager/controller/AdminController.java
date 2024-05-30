package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.AdminApi;
import bio.terra.appmanager.api.model.Chart;
import bio.terra.appmanager.api.model.ChartArray;
import bio.terra.appmanager.service.ChartService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

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
  public ResponseEntity<Void> deleteChart(String body) {
    this.chartService.deleteVersion(body);
    return ResponseEntity.noContent().build();
  }

  // Note that this method's implementation relies on `includeAll` having a default value and being
  // not null
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
