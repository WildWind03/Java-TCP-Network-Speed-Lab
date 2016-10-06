package ru.chirikhin.tcp2speed.client;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client implements Runnable {
    private final static Logger logger = Logger.getLogger(Client.class);
    private final static int BUFFER_SIZE = 1024;

    private final SocketChannel channel;

    public Client(String ip, int port) {
        if (null == ip) {
            throw new IllegalArgumentException("ip can not be null");
        }

        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative");
        }

        try {
            channel = SocketChannel.open();
            channel.connect(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create socket", e);
        }
    }

    public void run() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        byteBuffer.clear();
        byteBuffer.put(new byte[BUFFER_SIZE]);
        byteBuffer.flip();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                byteBuffer.clear();
                byteBuffer.put(new byte[BUFFER_SIZE]);
                byteBuffer.flip();

                while(byteBuffer.hasRemaining()){
                    channel.write(byteBuffer);
                }
            }
        } catch (IOException e) {
            logger.error("Can't send symbol");
        }
    }
}
