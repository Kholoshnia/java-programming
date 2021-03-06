package ru.lab.server.controller.controllers.command.factory.exceptions;

public class CommandFactoryException extends Exception {
  public CommandFactoryException() {
    super();
  }

  public CommandFactoryException(String message) {
    super(message);
  }

  public CommandFactoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public CommandFactoryException(Throwable cause) {
    super(cause);
  }
}
