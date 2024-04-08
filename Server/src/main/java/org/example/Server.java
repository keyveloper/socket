package org.example;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server {
    public final int tcpServerPort = 9999;
    @Getter
    private final IdManager idManager = new IdManager();
    @Getter
    private final CountManager countManager = new CountManager();
    @Getter
    private final HandlerManger handlerManger = new HandlerManger();

    private final ServiceGiver serviceGiver = new ServerServiceGiver(this);

    public void start() {
        try {
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(new InetSocketAddress(tcpServerPort));
                System.out.println("Starting tcp Server: " + tcpServerPort);
                System.out.println("[ Waiting ]\n");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    handlerManger.register(clientSocket, clientHandler);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void service(Message message) throws IOException {
        MessageType messageType = MessageProcessor.makeMessageType(message);
        serviceGiver.service(message, messageType);
    }
}
