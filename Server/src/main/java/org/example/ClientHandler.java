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

                server.processMessage(message, clientSocket);

                if (message.getMessageType() == MessageTypeLibrary.FIN) {
                    System.out.println("closed ClientHandler");
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client closed the connection.");
        } catch (SocketException e) {
            System.out.println("Client connection was reset. ");
            Message finMessage = new Message(MessageTypeLibrary.FIN, null);
            server.processMessage(finMessage, clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void sendTypeOnly(MessageTypeLibrary messageType){
        try {
            byte[] packet = HeaderAdder.addOnlyTypeHeader(messageType);
            System.out.println("sendTypeonly packet" + Arrays.toString(packet));
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(packet.length);
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFile(byte[] body) {
        try {
            byte[] packet = HeaderAdder.addHeader(MessageTypeLibrary.FILE, body);
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(packet.length);
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getBodyLength(DataInputStream dataInputStream) {
        try {
            return dataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageTypeLibrary getMessageType(DataInputStream dataInputStream) {
        try {
            return MessageTypeLibrary.values()[dataInputStream.readInt()];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
