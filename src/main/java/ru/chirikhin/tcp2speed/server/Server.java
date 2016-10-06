package ru.chirikhin.tcp2speed.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server implements Runnable {

    private static final Logger logger = Logger.getLogger(Server.class);
    private static final int MILLIS_TO_COUNT_SPEED = 2000;

    private final int COUNT_OF_BYTES = 1024;
    private final ByteBuffer byteBuffer = ByteBuffer.allocate(COUNT_OF_BYTES);

    private final HashMap<Channel, Client> clientChannelHashMap = new HashMap<Channel, Client>();
    private final Selector selector;
    private final ServerSocket serverSocket;

    public Server(int port) {
        if (port < 0) {
            throw new IllegalArgumentException("Port can not be negative");
        }

        try {
            serverSocket = new ServerSocket(port);
            ServerSocketChannel serverSocketChannel = serverSocket.getChannel();
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not create socket or selector", e);
        }
    }

    public void run() {
        long startTime = System.currentTimeMillis();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                long timeoutForSelect = System.currentTimeMillis() - startTime;
                selector.select(timeoutForSelect);

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey selectionKey = keyIterator.next();
                    if (selectionKey.isAcceptable()) {
                        Socket socket = serverSocket.accept();
                        clientChannelHashMap.put(selectionKey.channel(), new Client(socket));
                    } else {
                        if (selectionKey.isReadable()) {
                            SocketChannel channel = (SocketChannel) selectionKey.channel();
                            Client client = clientChannelHashMap.get(channel);
                            int countOfReadBytes = channel.read(byteBuffer); // TODO: 06.10.16 ПРОЧИТАЛИ, НО В КАНАЛЕ ОСТАЛОСЬ ЧТО_ТО
                            byteBuffer.rewind();
                            client.addBytes(countOfReadBytes);
                        }
                     }
                }

                if (System.currentTimeMillis() - startTime > MILLIS_TO_COUNT_SPEED)
                    for (Map.Entry<Channel, Client> client : clientChannelHashMap.entrySet()) {
                        System.out.println(client.getValue().getHostName() + " - " + (client.getValue().getCountOfBytes() / MILLIS_TO_COUNT_SPEED) + "байт/c");
                        client.getValue().clearReadBytes();
                    }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
