package ru.lab.client.controller.argumentFormer.argumentFormers;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab.client.controller.argumentFormer.ArgumentValidator;
import ru.lab.client.controller.argumentFormer.exceptions.CancelException;
import ru.lab.client.controller.argumentFormer.exceptions.WrongArgumentsException;
import ru.lab.client.controller.validator.exceptions.ValidationException;
import ru.lab.client.view.console.Console;
import ru.lab.common.ArgumentMediator;
import ru.lab.common.CommandMediator;

import java.util.*;

public final class NewWorkerKeyFormer extends WorkerFormer {
  private static final Logger logger = LogManager.getLogger(NewWorkerKeyFormer.class);

  private String wrongArgumentsNumberException;

  @Inject
  public NewWorkerKeyFormer(
      CommandMediator commandMediator,
      Console console,
      Map<String, ArgumentValidator> validatorMap,
      ArgumentMediator argumentMediator) {
    super(commandMediator, console, validatorMap, argumentMediator);
  }

  @Override
  public void changeLocale(Locale locale) {
    super.changeLocale(locale);
    ResourceBundle resourceBundle = ResourceBundle.getBundle("localized.NewWorkerKeyFormer");

    wrongArgumentsNumberException = resourceBundle.getString("exceptions.wrongArgumentsNumber");
  }

  @Override
  public void check(List<String> arguments) throws WrongArgumentsException {
    if (arguments.size() != 1) {
      logger.warn(() -> "Got wrong arguments number.");
      throw new WrongArgumentsException(wrongArgumentsNumberException);
    }

    try {
      checkArgument(argumentMediator.workerKey, arguments.get(0));
    } catch (ValidationException e) {
      logger.warn(() -> "Got wrong argument.", e);
      throw new WrongArgumentsException(e.getMessage());
    }
  }

  @Override
  public Map<String, String> form(List<String> arguments) throws CancelException {
    Map<String, String> allArguments = new HashMap<>();
    allArguments.put(argumentMediator.workerKey, arguments.get(0));

    Map<String, String> workerArguments = formWorker();
    logger.info(() -> "Worker arguments were formed.");

    allArguments.putAll(workerArguments);

    logger.info(() -> "All arguments were formed.");
    return allArguments;
  }
}
