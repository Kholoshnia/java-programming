package ru.lab.server.controller.controllers.command.commands.special;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.lab.common.ArgumentMediator;
import ru.lab.common.CommandMediator;
import ru.lab.common.exitManager.ExitManager;
import ru.lab.common.transfer.response.Response;
import ru.lab.server.controller.services.script.Script;
import ru.lab.server.controller.services.script.scriptExecutor.ScriptExecutor;
import ru.lab.server.model.domain.entity.entities.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class ExecuteScriptCommand extends SpecialCommand {
  private static final Logger logger = LogManager.getLogger(ExecuteScriptCommand.class);

  public ExecuteScriptCommand(
      Configuration configuration,
      CommandMediator commandMediator,
      ArgumentMediator argumentMediator,
      Map<String, String> arguments,
      User user,
      Locale locale,
      ExitManager exitManager,
      ScriptExecutor scriptExecutor) {
    super(
        configuration,
        commandMediator,
        argumentMediator,
        arguments,
        user,
        locale,
        exitManager,
        scriptExecutor);
  }

  @Override
  public Response executeCommand() {
    List<String> lines = new ArrayList<>(arguments.values());
    Script script = new Script(locale, user, lines);

    logger.info(() -> "Executing script...");
    Response response = scriptExecutor.execute(script);

    logger.info(() -> "Script was executed.");
    return response;
  }
}
