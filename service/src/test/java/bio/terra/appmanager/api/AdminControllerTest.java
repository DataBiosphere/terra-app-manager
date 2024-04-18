package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.appmanager.controller.AdminController;
import bio.terra.appmanager.service.ChartService;
import bio.terra.common.iam.BearerToken;
import bio.terra.common.iam.SamUser;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@ContextConfiguration(classes = AdminController.class)
@WebMvcTest
public class AdminControllerTest {
  @MockBean ChartService serviceMock;

  @Autowired private MockMvc mockMvc;

  private SamUser testUser =
      new SamUser(
          "test@email",
          UUID.randomUUID().toString(),
          new BearerToken(UUID.randomUUID().toString()));

  @BeforeEach
  void beforeEach() {}

  @Test
  void testGetMessageOk() throws Exception {
    String chartName = "chart-name-here";
    String chartVersion = "chart-version-here";
    ArgumentCaptor<List<bio.terra.appmanager.model.ChartVersion>> argument =
        ArgumentCaptor.forClass(List.class);

    mockMvc
        .perform(
            post("/api/admin/v1/charts/versions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "[{"
                        + "\"chartName\": \""
                        + chartName
                        + "\","
                        + "\"chartVersion\": \""
                        + chartVersion
                        + "\""
                        + "}]"))
        .andExpect(status().isNoContent());

    verify(serviceMock).createVersions(argument.capture());
    assert (argument.getValue().size() == 1);
    verifyChartVersion(argument.getValue().get(0), chartName, chartVersion, null, null, null);
  }

  private void verifyChartVersion(
      bio.terra.appmanager.model.ChartVersion version,
      String chartName,
      String chartVersion,
      String appVersion,
      Date activeAt,
      Date inactiveAt) {
    assertEquals(version.chartName(), chartName);
    assertEquals(version.chartVersion(), chartVersion);
    assertEquals(version.appVersion(), appVersion);
    assertEquals(version.activeAt(), activeAt);
    assertEquals(version.inactiveAt(), inactiveAt);
  }
}
