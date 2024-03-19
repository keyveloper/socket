package org.example;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            dataInputStream = new DataInputStream(clientSocket.getInputStream());
            dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.server = server;
        System.out.println("Start c-handler");
    }
    @Override
    public void run(){
        try {
            while (true) {
                // body length
                int bodyLength = dataInputStream.readInt();
                server.print(String.valueOf(bodyLength));

                // byte[4] = length(only body)
                int typeInt = dataInputStream.readInt();
                MessageType messageType = MessageType.values()[typeInt];
                server.print("Message Type: " + messageType);

                //byte[n] = body
                byte[] inMessageByte = new byte[bodyLength];
                dataInputStream.readFully(inMessageByte);

                Message message = new Message(messageType, inMessageByte, clientSocket);
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

    public void sendPacket(MessageType messageType, String message){
        byte[] sendingByte = Share.getPacketHeader(messageType, message);
        try {
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendByte(byte[] body) {
        try {
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
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }
}
