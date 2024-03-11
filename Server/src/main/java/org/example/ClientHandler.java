package org.example;

import java.io.*;
import java.net.Socket;

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

                // All byteLength
                int inAllLength = dataInputStream.readInt();

                // byte[4] = length(only body)
                byte[] inLengthByte = new byte[4];
                dataInputStream.readFully(inLengthByte);
                // int messageLength = Share.readInputLength(inLengthByte);

                //byte[4] = type
                byte[] inTypeByte = new byte[4];
                dataInputStream.readFully(inTypeByte);
                MessageType messageType = Share.readInputType(inTypeByte);

                //byte[n] = body
                byte[] inMessageByte = new byte[inAllLength - 8];
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
        byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, message);
        try {
            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTypeOnly(MessageType messageType){
        byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, "");
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
