package ru.chirikhin.tcp2speed.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedList;

public class Server implements Runnable {

    private static final Logger logger = Logger.getLogger(Server.class);

    private final ServerSocketChannel serverSocketChannel;
    private final LinkedList<Socket> socketLinkedList = new LinkedList<Socket>();
    private final LinkedList<SelectionKey> selectionKeyLinkedList= new LinkedList<SelectionKey>();
    private final Selector selector;

    public Server(int port) {
        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative");
        }

        try {
            ServerSocket socket = new ServerSocket(port);
            serverSocketChannel = socket.getChannel();
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            selectionKeyLinkedList.add(serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT));
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create socket or selector", e);
        }
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int readyChannels = selector.select();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
