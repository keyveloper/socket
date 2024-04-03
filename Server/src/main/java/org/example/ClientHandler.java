package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    private final Server server;

    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println("Start c-handler");
    }
    @Override
    public void run(){
        try {
            ServerPacketReader serverPacketReader = new ServerPacketReader(clientSocket);
            ServerPacketSender serverPacketSender = new ServerPacketSender(clientSocket);

            while (true) {
                Message message = serverPacketReader.readPacket();
                server.processMessage(message);

            }
        } catch (EOFException e) {
            System.out.println("Client closed the connection.");
        } catch (SocketException e) {
            // socket이 예상치 못하게 종료
            System.out.println("Client connection was reset. ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
