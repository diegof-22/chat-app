package server;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.time.*;
import java.time.format.*;

public class ChatServer {
    private static final int PORT = 12345;

    static final String RESET = "\u001B[0m";
    static final String BOLD = "\u001B[1m";
    static final String CYAN = "\u001B[96m";
    static final String GREEN = "\u001B[92m";
    static final String GRAY = "\u001B[90m";

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static Set<ClientHandler> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        printBanner();
        log("Server started on port " + PORT);
        log("Waiting for connections...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String ip = clientSocket.getInetAddress().getHostAddress();
                log("Incoming connection from " + ip);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }

    public static void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static int getClientCount() {
        return clients.size();
    }

    public static void log(String message) {
        String time = LocalTime.now().format(TIME_FMT);
        System.out.println(GRAY + "[" + time + "]" + RESET + GREEN + " [SERVER] " + RESET + message);
    }

    static void printBanner() {
        System.out.println(CYAN + BOLD +
                "\n  ╔══════════════════════════════════════╗\n" +
                "  ║       JAVA GAME CHAT  -  SERVER      ║\n" +
                "  ║            Port: 12345               ║\n" +
                "  ╚══════════════════════════════════════╝\n" + RESET);
    }
}
