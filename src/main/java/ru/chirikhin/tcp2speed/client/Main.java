package ru.chirikhin.tcp2speed.client;

public class Main {
    private static int PORT = 12763;
    private static String DEFAULT_HOST = "127.0.0.1";

    public static void main(String[] args) {
        try {
            Client client = new Client(DEFAULT_HOST, PORT);
            Thread clientThread = new Thread(client);
            clientThread.start();
            clientThread.join();

        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println("Error" + t.getMessage());
        }
    }
}
