package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your username: ");
            username = in.readLine();
            out.println("/help to see commands");
            ChatServer.broadcast("User " + username + " has joined the chat", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/")) {
                    handleCommand(message);
                    if (message.equalsIgnoreCase("/quit")) break;
                } else {
                    System.out.println("You: " + message);
                    ChatServer.broadcast(username + ": " + message, this);
                }
            }
        } catch (IOException e) {

        } finally {
            ChatServer.removeClient(this);
            ChatServer.broadcast("User " + username + " has left the chat", this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }


    public void handleCommand(String message){
        String command = message.toLowerCase();
        
        switch (command) {
            case "/help":
                sendMessage("--- Commands ---");
                sendMessage("/help - Show this message");
                sendMessage("/users - Show online users");
                sendMessage("/quit - Disconnect from server");
                sendMessage("----------------");
                break;
            case "/users":
                sendMessage("Users now online: " + ChatServer.getClientCount());
                break;
            case "/quit":
                sendMessage("Disconnecting from server...");
                break;
            default:
                sendMessage("Command not found. Type /help to see commands");
                break;
        }   
    }
}
