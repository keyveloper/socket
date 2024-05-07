package org.example;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.types.MessageType;
import java.io.*;
import java.net.*;


@NoArgsConstructor
@Getter
@Setter
public class Server {
    public final int tcpServerPort = 9999;
    private final HandlerManger handlerManger = new HandlerManger();
    private final ServiceGiver serviceGiver = new ServerServiceGiver(handlerManger);

    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("[ Waiting ]\n");
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " +
                        clientSocket.getRemoteSocketAddress().toString() + "\n");

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


