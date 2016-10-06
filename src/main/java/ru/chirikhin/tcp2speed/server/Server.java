package ru.chirikhin.tcp2speed.server;

import org.apache.log4j.Logger;
import sun.awt.image.ImageWatched;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server implements Runnable {

    private static final Logger logger = Logger.getLogger(Server.class);
    private static final int MILLIS_TO_COUNT_SPEED = 1000;

    private final int COUNT_OF_BYTES = 1024 * 1024;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(COUNT_OF_BYTES);

    private final LinkedList<Client> clientLinkedList = new LinkedList<>();
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public Server(int port) {
        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative");
        }

        try {
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create socket or selector", e);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                long timeoutForSelect = MILLIS_TO_COUNT_SPEED - (System.currentTimeMillis() - startTime);

                if (timeoutForSelect < 0) {
                    startTime = System.currentTimeMillis();

                    for (Client client : clientLinkedList) {
                        System.out.println(client.getHostName() + ": " +  (double) client.getCountOfBytes() / (1024 * 1024 * 1024) + " Гбайт/c");
                        client.clearReadBytes();
                    }

                    continue;
                }

                selector.select(timeoutForSelect);

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        logger.info("New client has been connected");

                        socketChannel.configureBlocking(false);
                        SelectionKey newSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                        Client client = new Client(socketChannel);
                        newSelectionKey.attach(client);
                        clientLinkedList.add(client);
                    } else {
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            Client client = (Client) selectionKey.attachment();
                            int countOfReadBytes;
                            countOfReadBytes = channel.read(byteBuffer);
                            logger.info ("New data has been read");
                            byteBuffer.rewind();
                            client.addBytes(countOfReadBytes);
                        }
                     }

                     keyIterator.remove();
                }
            }
        } catch (IOException e) {
            logger.error("IO Exception" + e.getMessage());
        }
    }
}
