package ru.chirikhin.tcp2speed.server;

import java.net.Socket;

public class Client {
    private final Socket socket;

    private int countOfBytes = 0;


    public Client(Socket socket) {
        if (null == socket) {
            throw new IllegalArgumentException("Socket can not be null");
        }

        this.socket = socket;
    }

    public void addBytes(int k) {
        countOfBytes += k;
    }

    public void clearReadBytes() {
        countOfBytes = 0;
    }

    public String getHostName() {
        return socket.getInetAddress().getHostName();
    }

    public int getCountOfBytes() {
        return countOfBytes;
    }
}
