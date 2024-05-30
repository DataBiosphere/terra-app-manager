package bio.terra.appmanager.controller;

import bio.terra.common.exception.NotFoundException;

public class ChartNotFoundException extends NotFoundException {
  public ChartNotFoundException(String message) {
    super(message);
  }
}
