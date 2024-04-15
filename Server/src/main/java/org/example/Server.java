package org.example;

import lombok.Getter;
import org.example.types.MessageType;
import java.io.*;
import java.net.*;


public class Server {
    public final int tcpServerPort = 9999;
    private final ServiceGiver serviceGiver = new ServerServiceGiver(this, new IdManager(), new CountManager());
    @Getter
    private final HandlerManger handlerManger = new HandlerManger();

    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("[ Waiting ]\n");
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                handlerManger.register(clientSocket, clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
                if (handlerManger.checkEmpty()) {
                    shutDown(serverSocket);
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void service(Message message) throws IOException {
        System.out.println("\n [service start] \n message : " + message);
        MessageType messageType = MessageProcessor.makeMessageType(message);
        serviceGiver.service(message, messageType);
    }

    private void shutDown(ServerSocket serverSocket) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server has benn shut down");
            }
        } catch (IOException e) {
            System.err.println("Error while shutting down the server: " + e.getMessage());
        }
    }

}


