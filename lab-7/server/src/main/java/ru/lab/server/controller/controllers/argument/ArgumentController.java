package ru.lab.server.controller.controllers.argument;

import com.google.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab.server.controller.controllers.argument.validator.ArgumentValidator;
import ru.lab.server.controller.controllers.argument.validator.exceptions.WrongNumberException;
import ru.lab.server.controller.controllers.argument.validator.exceptions.WrongValueException;
import ru.lab.common.CommandMediator;
import ru.lab.common.transfer.Request;
import ru.lab.common.transfer.response.Response;
import ru.lab.common.transfer.response.Status;
import ru.lab.server.controller.Controller;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class ArgumentController implements Controller {
  private static final Logger logger = LogManager.getLogger(ArgumentController.class);

  private final CommandMediator commandMediator;
  private final Map<String, ArgumentValidator> validatorMap;

  private String noSuchCommandAnswer;
  private String wrongArgumentsNumber;
  private String wrongArgumentsValue;

  @Inject
  public ArgumentController(
      CommandMediator commandMediator, Map<String, ArgumentValidator> validatorMap) {
    this.commandMediator = commandMediator;
    this.validatorMap = validatorMap;
  }

  private void changeLocale(Locale locale) {
    ResourceBundle resourceBundle =
        ResourceBundle.getBundle("localized.ArgumentController", locale);

    noSuchCommandAnswer = resourceBundle.getString("answers.notSuchCommand");
    wrongArgumentsNumber = resourceBundle.getString("answers.wrongArgumentsNumber");
    wrongArgumentsValue = resourceBundle.getString("answers.wrongArgumentsValue");
  }

  @Override
  public Response handle(Request request) {
    changeLocale(request.getLocale());

    String command = request.getCommand();

    if (!commandMediator.contains(command)) {
      logger.info("No such command: {}.", () -> command);
      return new Response(Status.BAD_REQUEST, noSuchCommandAnswer);
    }

    ArgumentValidator argumentValidator = validatorMap.get(command);

    if (argumentValidator == null) {
      logger.info(() -> "Got null arguments validator.");
      return new Response(Status.BAD_REQUEST, noSuchCommandAnswer);
    }

    try {
      argumentValidator.check(request.getArguments());
    } catch (WrongNumberException e) {
      logger.info(() -> "Got wrong arguments number", e);
      return new Response(Status.BAD_REQUEST, wrongArgumentsNumber);
    } catch (WrongValueException e) {
      logger.info(() -> "Got wrong arguments value", e);
      return new Response(Status.BAD_REQUEST, wrongArgumentsValue);
    }

    return null;
  }
}
