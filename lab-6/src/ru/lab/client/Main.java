package ru.lab.client;

import ru.lab.client.modules.Client;

public class Main {
  public static void main(String[] args) {
    Client client = new Client("localhost", 4356);
    if (client.connect()) {
      client.setup();
      client.run();
      client.close();
    }
  }
}
