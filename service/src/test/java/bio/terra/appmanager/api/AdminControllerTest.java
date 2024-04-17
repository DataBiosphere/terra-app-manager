package bio.terra.appmanager.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bio.terra.appmanager.controller.AdminController;
import bio.terra.appmanager.model.ChartVersion;
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
    ArgumentCaptor<List<ChartVersion>> argument = ArgumentCaptor.forClass(List.class);

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

    verify(serviceMock).createVersion(argument.capture());
    assert (argument.getValue().size() == 1);
    verifyChartVersion(argument.getValue().get(0), chartName, chartVersion, null, null, null);
  }

  private void verifyChartVersion(
      ChartVersion version,
      String chartName,
      String chartVersion,
      String appVersion,
      Date activeAt,
      Date inactiveAt) {
    assertEquals(version.getChartName(), chartName);
    assertEquals(version.getChartVersion(), chartVersion);
    assertEquals(version.getAppVersion(), appVersion);
    assertEquals(version.getActiveAt(), activeAt);
    assertEquals(version.getInactiveAt(), inactiveAt);
  }

  //  @Test
  //  void testGetMessageNotFound() throws Exception {
  //    when(serviceMock.getExampleForUser(testUser.getSubjectId())).thenReturn(Optional.empty());
  //
  //    mockMvc.perform(get("/api/example/v1/message")).andExpect(status().isNotFound());
  //  }
  //
  //  @Test
  //  void testIncrementCounter() throws Exception {
  //    var meterRegistry = new SimpleMeterRegistry();
  //    Metrics.globalRegistry.add(meterRegistry);
  //
  //    try {
  //      final String tagValue = "tag_value";
  //      mockMvc
  //          .perform(
  //              post("/api/example/v1/counter")
  //                  .contentType(MediaType.APPLICATION_JSON)
  //                  .content(tagValue))
  //          .andExpect(status().isNoContent());
  //
  //      var counter =
  //          meterRegistry
  //              .find(ExampleController.EXAMPLE_COUNTER_NAME)
  //              .tags(ExampleController.EXAMPLE_COUNTER_TAG, tagValue)
  //              .counter();
  //
  //      assertNotNull(counter);
  //      assertEquals(counter.count(), 1);
  //
  //    } finally {
  //      Metrics.globalRegistry.remove(meterRegistry);
  //    }
  //  }
}
