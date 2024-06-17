package bio.terra.appmanager.controller;

import bio.terra.appmanager.api.AdminApi;
import bio.terra.appmanager.api.model.Chart;
import bio.terra.appmanager.api.model.ChartArray;
import bio.terra.appmanager.config.SamConfiguration;
import bio.terra.appmanager.iam.SamService;
import bio.terra.appmanager.service.ChartService;
import bio.terra.common.iam.BearerTokenFactory;
import bio.terra.common.iam.SamUser;
import bio.terra.common.iam.SamUserFactory;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class AdminController implements AdminApi {
  private final BearerTokenFactory bearerTokenFactory;
  private final SamUserFactory samUserFactory;
  private final SamConfiguration samConfiguration;
  private final HttpServletRequest request;

  private final ChartService chartService;
  private final SamService samService;

  public AdminController(
      BearerTokenFactory bearerTokenFactory,
      SamUserFactory samUserFactory,
      SamConfiguration samConfiguration,
      HttpServletRequest request,
      SamService samService,
      ChartService chartService) {
    this.bearerTokenFactory = bearerTokenFactory;
    this.samUserFactory = samUserFactory;
    this.samConfiguration = samConfiguration;
    this.request = request;
    this.samService = samService;
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

  // Note that this method's implementation relies on `includeAll` having a default value and being
  // not null
  private SamUser getUser() {
    // this automatically checks if the user is enabled
    return this.samUserFactory.from(request, samConfiguration.basePath());
  }

  @Override
  public ResponseEntity<ChartArray> getCharts(String chartName, Boolean includeAll) {
    request
        .getHeaderNames()
        .asIterator()
        .forEachRemaining(name -> System.out.println(name + ": " + request.getHeader(name)));

    //    System.out.println(samUser.getEmail());
    //    System.out.println(samUser.getSubjectId());
    //    if (!samUser.getEmail().equals("leonardo-dev@broad-dsde-dev.iam.gserviceaccount.com")) {
    //      return ResponseEntity.status(417).build();
    //    }

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
