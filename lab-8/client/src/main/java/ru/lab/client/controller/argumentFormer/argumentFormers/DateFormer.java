package ru.lab.client.controller.argumentFormer.argumentFormers;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab.client.controller.argumentFormer.ArgumentValidator;
import ru.lab.client.controller.argumentFormer.exceptions.WrongArgumentsException;
import ru.lab.client.controller.localeManager.LocaleListener;
import ru.lab.client.controller.validator.exceptions.ValidationException;
import ru.lab.client.view.console.Console;
import ru.lab.common.ArgumentMediator;
import ru.lab.common.CommandMediator;

import java.util.*;

public final class DateFormer extends Former implements LocaleListener {
  private static final Logger logger = LogManager.getLogger(DateFormer.class);

  private final ArgumentMediator argumentMediator;

  private String wrongArgumentsNumberException;

  @Inject
  public DateFormer(
      CommandMediator commandMediator,
      Console console,
      Map<String, ArgumentValidator> validatorMap,
      ArgumentMediator argumentMediator) {
    super(commandMediator, console, validatorMap);
    this.argumentMediator = argumentMediator;
  }

  @Override
  public void changeLocale(Locale locale) {
    ResourceBundle resourceBundle = ResourceBundle.getBundle("localized.DateFormer");

    wrongArgumentsNumberException = resourceBundle.getString("exceptions.wrongArgumentsNumber");
  }

  @Override
  public void check(List<String> arguments) throws WrongArgumentsException {
    if (arguments.size() != 1) {
      logger.warn(() -> "Got wrong arguments number.");
      throw new WrongArgumentsException(wrongArgumentsNumberException);
    }

    try {
      checkArgument(argumentMediator.workerStartDate, arguments.get(0));
    } catch (ValidationException e) {
      logger.warn(() -> "Got wrong argument.", e);
      throw new WrongArgumentsException(e.getMessage());
    }
  }

  @Override
  public Map<String, String> form(List<String> arguments) {
    Map<String, String> allArguments = new HashMap<>();
    allArguments.put(argumentMediator.workerStartDate, arguments.get(0));

    logger.info(() -> "All arguments were formed.");
    return allArguments;
  }
}
