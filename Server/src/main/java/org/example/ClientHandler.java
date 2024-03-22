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
                System.out.println("\n\n\n packet received!!!!");
                int bodyLength = getBodyLength(dataInputStream);
                server.print("bodyLength: " + bodyLength);

                MessageType messageType = getMessageType(dataInputStream);
                server.print("Message Type: " + messageType);

                byte[] body = getBody(dataInputStream, bodyLength);
                //server.print("body: " + Arrays.toString(body));

                Message message = new Message(messageType, body, clientSocket);
                server.processMessage(message);

                if (messageType == MessageType.FIN){
                    break;
                }
            }
        } catch (EOFException e) {
            System.out.println("Client closed the connection.");
        } catch (SocketException e) {
            System.out.println("Client connection was reset. ");
            Message finMessage = new Message(MessageType.FIN, null, clientSocket);
            server.processMessage(finMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getBodyLength(DataInputStream dataInputStream) {
        int bodyLength;
        try {
            bodyLength = dataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bodyLength;
    }

    private MessageType getMessageType(DataInputStream dataInputStream) {
        int typeInt;
        try {
            typeInt = dataInputStream.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return MessageType.values()[typeInt];
    }

    private byte[] getBody(DataInputStream dataInputStream, int bodyLength) {
        byte[] body = new byte[bodyLength];
        try {
            dataInputStream.readFully(body, 0, bodyLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    public void sendPacket(MessageType messageType, String message){
        byte[] sendingByte = Share.getPacketHeader(messageType, message);
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendByte(byte[] body) {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(body.length);
            dataOutputStream.write(body, 0, body.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTypeOnly(MessageType messageType){
        byte[] sendingByte = Share.getPacketHeader(messageType, "");
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendFile(MessageType messageType, byte[] body) {
        byte[] sendingByte = Share.getPacketHeaderVerByte(messageType, body);
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
