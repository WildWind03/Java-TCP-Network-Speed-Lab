package ru.chirikhin.tcp2speed.client;

import org.apache.log4j.Logger;

public class Main {
    private final static int SERVER_PORT = 1234;
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            if (args.length > 1) {
                throw new IllegalArgumentException("Too many args");
            }

            Client client = new Client(args[0], SERVER_PORT);
            Thread clientThread = new Thread(client);
            clientThread.start();
            clientThread.join();

        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
    }
}
