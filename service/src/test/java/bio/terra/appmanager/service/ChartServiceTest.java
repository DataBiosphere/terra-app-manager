package bio.terra.appmanager.service;

import bio.terra.appmanager.BaseSpringBootTest;
import bio.terra.appmanager.dao.ChartVersionDao;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ChartServiceTest extends BaseSpringBootTest {
  @MockBean ChartVersionDao chartVersionDao;

  @Autowired ChartService chartService;

  @Test
  void createChartVersion_withEmptyList() {
    chartService.createVersions(List.of());
  }
}
