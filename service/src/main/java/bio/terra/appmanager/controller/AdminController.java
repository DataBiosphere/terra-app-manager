package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.AdminApi;
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

    ;

    this.chartService.createVersions(
        body.stream().map((bio.terra.appmanager.model.ChartVersion::fromApi)).toList());
    return ResponseEntity.noContent().build();
  }
}
