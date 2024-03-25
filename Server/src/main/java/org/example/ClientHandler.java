package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
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
            while (true) {
                DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());

                // body length
                int bodyLength = getBodyLength(dataInputStream);
                MessageType messageType = getMessageType(dataInputStream);
                byte[] body = new byte[bodyLength];
                dataInputStream.readFully(body);
                System.out.println("bodyLength: " + bodyLength + "\n messageType: " + messageType + "\nbody: " + Arrays.toString(body));

                Message message = new Message(messageType, body);
                server.processMessage(message, clientSocket);

                if (message.getMessageType() == MessageType.FIN) {
                    System.out.println("closed ClientHandler");
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client closed the connection.");
        } catch (SocketException e) {
            System.out.println("Client connection was reset. ");
            Message finMessage = new Message(MessageType.FIN, null);
            server.processMessage(finMessage, clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(MessageType messageType, byte[] body){
        try {
            byte[] packet = HeaderMaker.makeHeader(messageType, body);
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(packet.length);
            dataOutputStream.write(packet, 0, packet.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTypeOnly(MessageType messageType){
        try {
            byte[] packet = HeaderMaker.makeOnlyTypeHeader(messageType);
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
            byte[] packet = HeaderMaker.makeHeader(MessageType.FILE, body);
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

    private MessageType getMessageType(DataInputStream dataInputStream) {
        try {
            return MessageType.values()[dataInputStream.readInt()];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
