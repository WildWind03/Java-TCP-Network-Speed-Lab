package ru.chirikhin.tcp2speed.server;

import java.nio.channels.SocketChannel;

public class Client {
    private final SocketChannel socketChannel;

    private long countOfBytes = 0;
    private long startMillis = 0;


    public Client(SocketChannel socketChannel) {
        if (null == socketChannel) {
            throw new IllegalArgumentException("Socket can not be null");
        }


        this.socketChannel = socketChannel;
    }

    public void addBytes(int k) {
        countOfBytes += k;
    }

    public void clearReadBytes() {
        countOfBytes = 0;
    }

    public String getHostName() {
        return socketChannel.socket().getInetAddress().getHostName();
    }

    public long getCountOfBytes() {
        return countOfBytes;
    }

    public void setTime(long startMillis) {
        this.startMillis = startMillis;
    }

    public long getPeriod(long currentTime) {
        return currentTime - startMillis;
    }
}
