package com.lab.commands.availableCommands;

import com.lab.commands.Command;
import com.lab.runner.Editor;
import com.lab.runner.Response;

import java.util.ArrayList;
import java.util.List;

/** Exit command class */
public final class ExitCommand implements Command {
  @Override
  public String getKey() {
    return "exit";
  }

  @Override
  public String getInfo() {
    return "завершить программу (без сохранения в файл)";
  }

  @Override
  public String getParameters() {
    return null;
  }

  /**
   * Stops program execution
   *
   * @return Response and correctness
   */
  @Override
  public Response execute(Editor editor) {
    List<String> response = new ArrayList<>();
    response.add("Завершение работы программы");
    editor.setRunning(false);
    return new Response(true, response, Response.Types.CORRECT);
  }
}
