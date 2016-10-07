package ru.chirikhin.tcp2speed.client;

import org.apache.log4j.Logger;

public class Main {
    private final static int SERVER_PORT = 1234;
    private final static String DEFAULT_HOST = "127.0.0.1";
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            Client client = new Client(DEFAULT_HOST, SERVER_PORT);
            Thread clientThread = new Thread(client);
            clientThread.start();
            clientThread.join();

        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
    }
}
