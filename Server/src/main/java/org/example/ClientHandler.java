package org.example;

import org.example.types.MessageTypeCode;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

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
            while (true) {
                Message message = serverPacketReader.readPacket();
                server.service(message);

                if (message.getMessageTypeCode() == MessageTypeCode.FIN) {
                    break;
                }
            }

        } catch (EOFException e) {
            System.out.println("Client closed the connection.");
        } catch (SocketException e) {
            // socket out accidently
            System.out.println("Client connection was reset. ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(byte[] packet) {
        System.out.println("in client Handler start send packet");
        ServerPacketSender serverPacketSender = new ServerPacketSender(clientSocket);
        serverPacketSender.sendPacket(packet);
    }

}
