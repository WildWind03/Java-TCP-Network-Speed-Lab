package ru.chirikhin.tcp2speed;

import org.apache.log4j.Logger;
import ru.chirikhin.tcp2speed.client.Client;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Main {
    private static int PORT = 12763;
    private static String DEFAULT_HOST = "127.0.0.1";

    private static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        Client client;
        try {
            client = new Client(DEFAULT_HOST, PORT);
        } catch (Throwable t) {
            logger.error(t.getMessage());
            return;
        }

        new Thread(client).start();
    }
}
