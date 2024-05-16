package bio.terra.appmanager.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bio.terra.common.exception.InconsistentFieldsException;
import org.junit.jupiter.api.Test;

public class ChartVersionValidationTest {

  @Test
  void testChartNameValidation() {
    String controlChartVersion = "chartVersion";
    String goodChartName1 = "good";
    String goodChartName2 = "also-good";
    String goodChartName3 = "numbers1";

    String badChartName1 = "upperCase";
    String badChartName2 = "specialchar$";
    String badChartName3 = "tooooooooooooooooooolooooooooooooooooooooooooooooooooooong";

    assertDoesNotThrow(() -> new ChartVersion(goodChartName1, controlChartVersion));
    assertDoesNotThrow(() -> new ChartVersion(goodChartName2, controlChartVersion));
    assertDoesNotThrow(() -> new ChartVersion(goodChartName3, controlChartVersion));
    Exception ex1 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(badChartName1, controlChartVersion));
    assertTrue(ex1.getMessage().contains(getPartialChartNameExceptionMessage(badChartName1)));
    Exception ex2 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(badChartName2, controlChartVersion));
    assertTrue(ex2.getMessage().contains(getPartialChartNameExceptionMessage(badChartName2)));
    Exception ex3 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(badChartName3, controlChartVersion));
    assertTrue(ex3.getMessage().contains(getPartialChartNameExceptionMessage(badChartName3)));
  }

  @Test
  void testChartVersionValidation() {
    String controlChartName = "chart-name";
    String goodChartVersion1 = "good";
    String goodChartVersion2 = "alsoGood";

    String badChartVersion1 = "Uppercase";
    String badChartVersion2 = "numbers1";
    String badChartVersion3 = "specialchar$";
    String badChartVersion4 = "dash-es";

    assertDoesNotThrow(() -> new ChartVersion(controlChartName, goodChartVersion1));
    assertDoesNotThrow(() -> new ChartVersion(controlChartName, goodChartVersion2));
    Exception ex1 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(controlChartName, badChartVersion1));
    assertTrue(ex1.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion1)));
    Exception ex2 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(controlChartName, badChartVersion2));
    assertTrue(ex2.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion2)));
    Exception ex3 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(controlChartName, badChartVersion3));
    assertTrue(ex3.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion3)));
    Exception ex4 =
        assertThrows(
            InconsistentFieldsException.class,
            () -> new ChartVersion(controlChartName, badChartVersion4));
    assertTrue(ex4.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion4)));
  }

  private String getPartialChartNameExceptionMessage(String chartName) {
    return "Chart name " + chartName + " is invalid";
  }

  private String getPartialChartVersionExceptionMessage(String chartVersion) {
    return "Chart version " + chartVersion + " is invalid";
  }
}
