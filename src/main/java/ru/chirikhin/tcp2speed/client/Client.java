package ru.chirikhin.tcp2speed.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {

    private static int SYMBOL_TO_SEND = 'a';
    private static Logger logger = Logger.getLogger(Client.class);

    private OutputStream outputStream;

    public Client(String ip, int port) {
        if (null == ip) {
            throw new IllegalArgumentException("ip can not be null");
        }

        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative");
        }

        try {
            Socket socket = new Socket(ip, port);
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create socket", e);
        }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                outputStream.write(SYMBOL_TO_SEND);
            }
        } catch (IOException e) {
            logger.error("Can't send symbol");
        }
    }
}
