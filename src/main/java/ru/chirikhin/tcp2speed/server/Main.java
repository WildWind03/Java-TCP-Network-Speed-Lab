package ru.chirikhin.tcp2speed.server;

public class Main {
    private static int PORT = 12763;

    public static void main(String[] args) {
        try {
            Server server = new Server(PORT);
            Thread serverThread = new Thread(server);
            serverThread.start();
            serverThread.join();
        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
    }
}
