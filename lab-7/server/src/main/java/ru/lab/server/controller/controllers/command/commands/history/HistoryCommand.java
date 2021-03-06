package ru.lab.server.controller.controllers.command.commands.history;

import org.apache.commons.configuration2.Configuration;
import ru.lab.server.controller.controllers.command.Command;
import ru.lab.server.model.domain.history.History;
import ru.lab.common.ArgumentMediator;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class HistoryCommand extends Command {
  protected final String historyIsEmptyAnswer;

  protected final Locale locale;
  protected final History history;

  public HistoryCommand(
      Configuration configuration,
      ArgumentMediator argumentMediator,
      Map<String, String> arguments,
      Locale locale,
      History history) {
    super(configuration, argumentMediator, arguments);
    this.locale = locale;
    this.history = history;

    ResourceBundle resourceBundle = ResourceBundle.getBundle("localized.HistoryCommand", locale);

    historyIsEmptyAnswer = resourceBundle.getString("answers.historyIsEmpty");
  }
}
