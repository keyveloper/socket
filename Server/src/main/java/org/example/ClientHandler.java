package org.example;

import lombok.Data;
import org.example.types.MessageTypeCode;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

@Data

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
                System.out.println("start read!");
                Message message = serverPacketReader.readPacket();
                server.service(message);

                if (message.getMessageTypeCode() == MessageTypeCode.FIN) {
                    break;
                }
            }

        } catch (EOFException e) {
            System.out.println("End of stream");
            Message outMessage = new Message(MessageTypeCode.FIN, null, clientSocket);
            try {
                server.service(outMessage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        } catch (SocketException e) {
            // socket out accidently
            System.out.println("Client connection was reset. ");
            Message outMessage = new Message(MessageTypeCode.FIN, null, clientSocket);
            try {
                server.service(outMessage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            System.out.println("IOException occur");
            Message outMessage = new Message(MessageTypeCode.FIN, null, clientSocket);
            try {
                server.service(outMessage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void sendPacket(byte[] packet) {
        System.out.println("in client Handler start send packet");
        ServerPacketSender serverPacketSender = new ServerPacketSender(clientSocket);
        serverPacketSender.sendPacket(packet);
    }

}
