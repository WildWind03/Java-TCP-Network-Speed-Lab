package ru.chirikhin.tcp2speed.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Server implements Runnable {

    private final static Logger logger = Logger.getLogger(Server.class);
    private final static int MILLIS_TO_COUNT_SPEED = 1000;
    private long maxId = 0;

    private final int COUNT_OF_BYTES = 1024 * 1024;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(COUNT_OF_BYTES);

    private final HashMap<Long, Client> clientHashMap = new HashMap<>();

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

                    for (Map.Entry<Long, Client> clientEntry: clientHashMap.entrySet()) {
                        Client client = clientEntry.getValue();
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

                        socketChannel.configureBlocking(false);

                        SelectionKey newSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
                        Client client = new Client(socketChannel);
                        long currentId = maxId++;

                        newSelectionKey.attach(currentId);
                        clientHashMap.put(currentId, client);
                    } else {
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            long currentId = (long) selectionKey.attachment();
                            Client client = clientHashMap.get(currentId);

                            int countOfReadBytes = channel.read(byteBuffer);

                            if (-1 == countOfReadBytes) {
                                channel.close();
                                clientHashMap.remove(currentId);
                            } else {
                                client.addBytes(countOfReadBytes);
                            }

                            byteBuffer.compact();
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
