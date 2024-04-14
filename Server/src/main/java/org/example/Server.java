package org.example;

import lombok.Getter;
import org.example.types.MessageType;
import org.example.types.MessageTypeCode;

import java.io.*;
import java.net.*;

public class Server {
    public final int tcpServerPort = 9999;
    private final ServiceGiver serviceGiver = new ServerServiceGiver(this, new IdManager(), new CountManager());
    @Getter
    private final HandlerManger handlerManger = new HandlerManger();

    public void start() {
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
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void service(Message message) throws IOException {
        MessageType messageType = MessageProcessor.makeMessageType(message);
        serviceGiver.service(message, messageType);
    }

}


