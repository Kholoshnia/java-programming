package ru.lab.server.controller.controllers.command.factory.factories;

import com.google.inject.Inject;
import org.apache.commons.configuration2.Configuration;
import ru.lab.server.controller.controllers.command.commands.modification.*;
import ru.lab.common.ArgumentMediator;
import ru.lab.common.CommandMediator;
import ru.lab.server.controller.controllers.command.Command;
import ru.lab.server.controller.controllers.command.commands.modification.*;
import ru.lab.server.controller.controllers.command.factory.CommandFactory;
import ru.lab.server.controller.controllers.command.factory.exceptions.CommandFactoryException;
import ru.lab.server.controller.services.parser.Parser;
import ru.lab.server.model.domain.entity.entities.user.User;
import ru.lab.server.model.domain.entity.entities.worker.Worker;
import ru.lab.server.model.domain.repository.Repository;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class ModificationCommandFactory extends CommandFactory {
  private final Configuration configuration;
  private final ArgumentMediator argumentMediator;
  private final Repository<Worker> workerRepository;
  private final Repository<User> userRepository;
  private final Parser parser;

  private final Map<String, Class<? extends ModificationCommand>> modificationCommandMap;

  @Inject
  public ModificationCommandFactory(
      Configuration configuration,
      ArgumentMediator argumentMediator,
      CommandMediator commandMediator,
      Repository<Worker> workerRepository,
      Repository<User> userRepository,
      Parser parser) {
    this.configuration = configuration;
    this.argumentMediator = argumentMediator;
    this.userRepository = userRepository;
    this.workerRepository = workerRepository;
    this.parser = parser;
    modificationCommandMap = initModificationCommandMap(commandMediator);
  }

  private Map<String, Class<? extends ModificationCommand>> initModificationCommandMap(
      CommandMediator commandMediator) {
    return new HashMap<String, Class<? extends ModificationCommand>>() {
      {
        put(commandMediator.insert, InsertCommand.class);
        put(commandMediator.update, UpdateCommand.class);
        put(commandMediator.removeKey, RemoveKeyCommand.class);
        put(commandMediator.clear, ClearCommand.class);
        put(commandMediator.removeLower, RemoveLowerCommand.class);
        put(commandMediator.replaceIfLower, ReplaceIfLowerCommand.class);
      }
    };
  }

  @Override
  public Command createCommand(
      String command, Map<String, String> arguments, Locale locale, String login)
      throws CommandFactoryException {
    Class<? extends ModificationCommand> clazz = modificationCommandMap.get(command);

    try {
      Constructor<? extends ModificationCommand> constructor =
          clazz.getConstructor(
              Configuration.class,
              ArgumentMediator.class,
              Map.class,
              Locale.class,
              Repository.class,
              Parser.class,
              User.class);

      return constructor.newInstance(
          configuration,
          argumentMediator,
          arguments,
          locale,
          workerRepository,
          parser,
          getUser(userRepository, login));
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new CommandFactoryException(e);
    }
  }
}
