package org.example;

import lombok.Data;
import org.example.types.MessageTypeCode;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;

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
                Message message = serverPacketReader.readPacket();
                if (message == null) {
                    System.out.println("message is null!!");
                    // [bodyLength, typeCode, finCodee, notice""]
                    int FIN_CODE_SIZE = 4;
                    ByteBuffer buffer = ByteBuffer.allocate(FIN_CODE_SIZE + "".length());
                    buffer.putInt(NoticeCode.FIN.ordinal());
                    buffer.put("".getBytes());
                    Message outMessage = new Message(MessageTypeCode.FIN, buffer.array(), clientSocket);
                    server.service(outMessage);
                    break;
                } else {
                    server.service(message);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPacket(byte[] packet) {
        ServerPacketSender serverPacketSender = new ServerPacketSender(clientSocket);
        serverPacketSender.sendPacket(packet);
    }

}
