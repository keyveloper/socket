package org.example;

import java.io.*;
import java.net.Socket;

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
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                OutputStream outputStream = clientSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                synchronized ( server.socketOutStreamLock ){
                    server.socketoutStreamMap.put(clientSocket, dataOutputStream);
                }

                // All byteLength
                int inAllLength = dataInputStream.readInt();

                // byte[4] = length(only body)
                byte[] inLengthByte = new byte[4];
                dataInputStream.readFully(inLengthByte);
                int messageLength = Share.readInputLength(inLengthByte);

                //byte[4] = type
                byte[] inTypeByte = new byte[4];
                dataInputStream.readFully(inTypeByte);
                MessageType messageType = Share.readInputType(inTypeByte);

                //byte[n] = body
                byte[] inMessageByte = new byte[inAllLength - 8];
                dataInputStream.readFully(inMessageByte);
                String message = Share.readInputMessage(inMessageByte);

                server.actionByType(messageType, message, clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}