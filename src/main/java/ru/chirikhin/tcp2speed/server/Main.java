package ru.chirikhin.tcp2speed.server;

import org.apache.log4j.Logger;

public class Main {
    private final static int PORT = 1234;
    private final static Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) {
        try {
            Server server = new Server(PORT);
            Thread serverThread = new Thread(server);
            serverThread.start();
            serverThread.join();
        } catch (Throwable t) {
            logger.error(t.getMessage());
        }
    }
}
