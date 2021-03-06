package ru.lab.client.view.console.exceptions;

import ru.lab.client.view.exceptions.ViewException;

public final class ConsoleException extends ViewException {
  public ConsoleException() {
    super();
  }

  public ConsoleException(String message) {
    super(message);
  }

  public ConsoleException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConsoleException(Throwable cause) {
    super(cause);
  }
}
