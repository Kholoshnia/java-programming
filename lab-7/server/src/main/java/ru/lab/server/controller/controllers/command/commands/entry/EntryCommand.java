package ru.lab.server.controller.controllers.command.commands.entry;

import org.apache.commons.configuration2.Configuration;
import ru.lab.server.controller.controllers.command.Command;
import ru.lab.server.controller.services.hash.HashGenerator;
import ru.lab.server.model.domain.entity.entities.user.User;
import ru.lab.server.model.domain.repository.Repository;
import ru.lab.common.ArgumentMediator;

import java.security.Key;
import java.util.Locale;
import java.util.Map;

public abstract class EntryCommand extends Command {
  protected final Locale locale;
  protected final Repository<User> userRepository;
  protected final HashGenerator hashGenerator;
  protected final Key key;
  protected final String subject;

  public EntryCommand(
      Configuration configuration,
      ArgumentMediator argumentMediator,
      Map<String, String> arguments,
      Locale locale,
      Repository<User> userRepository,
      HashGenerator hashGenerator,
      Key key) {
    super(configuration, argumentMediator, arguments);
    this.locale = locale;
    this.userRepository = userRepository;
    this.hashGenerator = hashGenerator;
    this.key = key;
    subject = configuration.getString("jwt.subject");
  }
}
