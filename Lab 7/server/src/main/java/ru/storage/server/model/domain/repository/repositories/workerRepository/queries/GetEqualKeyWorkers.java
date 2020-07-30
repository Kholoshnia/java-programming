package ru.storage.server.model.domain.repository.repositories.workerRepository.queries;

import ru.storage.server.model.domain.entity.entities.worker.Worker;
import ru.storage.server.model.domain.repository.Query;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Returns workers collection copy. The copy contains a workers with a key equal to the specified.
 */
public final class GetEqualKeyWorkers implements Query<Worker> {
  private final int key;

  /**
   * Creates a query to get workers with the specified key.
   *
   * @param key worker key
   */
  public GetEqualKeyWorkers(int key) {
    this.key = key;
  }

  @Override
  public List<Worker> execute(List<Worker> workers) {
    return workers.stream().filter(worker -> worker.getKey() == key).collect(Collectors.toList());
  }
}