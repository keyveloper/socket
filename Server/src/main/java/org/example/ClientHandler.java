package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Server server;
    private DataOutputStream dataOutputStream;

    public ClientHandler(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
        System.out.println("Start c-handler");
    }
    @Override
    public void run(){
        try {
            while (true) {
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);

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
        } catch (IOException e) {
            throw new RuntimeException(e);
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
