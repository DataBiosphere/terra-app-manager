package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.AdminApi;
import bio.terra.appmanager.api.model.ChartArray;
import bio.terra.appmanager.api.model.ChartVersion;
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
  public ResponseEntity<Void> createChartVersions(List<ChartVersion> body) {
    List<bio.terra.appmanager.model.ChartVersion> versions = List.of();
    try {
      versions = body.stream().map((bio.terra.appmanager.model.ChartVersion::fromApi)).toList();
    } catch (NullPointerException npe) {
      return ResponseEntity.badRequest().build();
    }

    this.chartService.createVersions(versions);
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteChartVersions(List<String> body) {
    this.chartService.deleteVersions(body);
    return ResponseEntity.noContent().build();
  }
  // Note that this method's implementation relies on `includeAll` having a default value and being
  // not null
  @Override
  public ResponseEntity<ChartArray> getChartVersions(String chartName, Boolean includeAll) {
    List<String> versions = chartName == null ? List.of() : List.of(chartName);
    List<bio.terra.appmanager.model.ChartVersion> dbResult =
        this.chartService.getVersions(versions, includeAll);

    List<bio.terra.appmanager.api.model.ChartVersion> apiChartVersions =
        dbResult.stream().map(bio.terra.appmanager.model.ChartVersion::toApi).toList();

    ChartArray apiResult = new ChartArray();
    apiResult.addAll(apiChartVersions);

    return ResponseEntity.ok(apiResult);
  }
}
