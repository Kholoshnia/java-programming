package ru.lab.server.app.connection;

public interface ServerProcessor {
  /**
   * Processes client.
   *
   * @param clientWorker client worker
   */
  void process(ClientWorker clientWorker);
}
