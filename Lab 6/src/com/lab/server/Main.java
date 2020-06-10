package com.lab.server;

import com.lab.common.io.OutputException;
import com.lab.server.modules.Server;

public class Main {
  public static void main(String[] args) throws OutputException {
    Server server = new Server(4356);
    if (server.getExecutor().setArgs(args)) {
      server.load();
      if (server.start()) {
        server.connect();
      }
      server.close();
    }
  }
}
