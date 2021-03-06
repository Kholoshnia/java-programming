package ru.lab.server.model.domain.entity.entities.worker;

import ru.lab.server.model.domain.entity.exceptions.ValidationException;

public enum Status {
  FIRED,
  HIRED,
  RECOMMENDED_FOR_PROMOTION;

  public static Status getStatus(String statusString) throws ValidationException {
    if (statusString == null) {
      return null;
    }

    Status[] statuses = values();

    for (Status status : statuses) {
      if (status.name().equals(statusString)) {
        return status;
      }
    }

    throw new ValidationException();
  }
}
