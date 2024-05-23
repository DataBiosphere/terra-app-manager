package bio.terra.appmanager.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bio.terra.common.exception.InconsistentFieldsException;
import org.junit.jupiter.api.Test;

class ChartValidationTest {

  @Test
  void testChartNameValidation() {
    String controlChartVersion = "version";
    String goodChartName1 = "good";
    String goodChartName2 = "also-good";
    String goodChartName3 = "numbers1";

    String badChartName1 = "upperCase";
    String badChartName2 = "specialchar$";
    String badChartName3 = "tooooooooooooooooooolooooooooooooooooooooooooooooooooooong";

    assertDoesNotThrow(() -> new Chart(goodChartName1, controlChartVersion));
    assertDoesNotThrow(() -> new Chart(goodChartName2, controlChartVersion));
    assertDoesNotThrow(() -> new Chart(goodChartName3, controlChartVersion));
    Exception ex1 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(badChartName1, controlChartVersion));
    assertTrue(ex1.getMessage().contains(getPartialChartNameExceptionMessage(badChartName1)));
    Exception ex2 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(badChartName2, controlChartVersion));
    assertTrue(ex2.getMessage().contains(getPartialChartNameExceptionMessage(badChartName2)));
    Exception ex3 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(badChartName3, controlChartVersion));
    assertTrue(ex3.getMessage().contains(getPartialChartNameExceptionMessage(badChartName3)));
  }

  @Test
  void testChartVersionValidation() {
    String controlChartName = "chart-name";
    String goodChartVersion1 = "good";
    String goodChartVersion2 = "alsoGood";

    String badChartVersion1 = "UpperCaseStart";
    String badChartVersion2 = "numbers1";
    String badChartVersion3 = "specialchar$";
    String badChartVersion4 = "dash-es";
    String badChartVersion5 = "tooooooooooooooooooolooooooooooooooooooooooooooooooooooong";
    String badChartVersion6 = "twoUPpercase";

    assertDoesNotThrow(() -> new Chart(controlChartName, goodChartVersion1));
    assertDoesNotThrow(() -> new Chart(controlChartName, goodChartVersion2));
    Exception ex1 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion1));
    assertTrue(ex1.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion1)));
    Exception ex2 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion2));
    assertTrue(ex2.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion2)));
    Exception ex3 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion3));
    assertTrue(ex3.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion3)));
    Exception ex4 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion4));
    assertTrue(ex4.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion4)));
    Exception ex5 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion5));
    assertTrue(ex5.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion5)));
    Exception ex6 =
        assertThrows(
            InconsistentFieldsException.class, () -> new Chart(controlChartName, badChartVersion6));
    assertTrue(ex6.getMessage().contains(getPartialChartVersionExceptionMessage(badChartVersion6)));
  }

  private String getPartialChartNameExceptionMessage(String chartName) {
    return "Chart name " + chartName + " is invalid";
  }

  private String getPartialChartVersionExceptionMessage(String chartVersion) {
    return "Chart version " + chartVersion + " is invalid";
  }
}
