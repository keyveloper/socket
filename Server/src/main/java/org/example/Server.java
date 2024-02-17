package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public final static int tcpServerPort = 9999;
    private final HashMap<String, Socket> idSocket = new HashMap<>();
    private final HashMap<Socket, String> socketId = new HashMap<>();
    private final HashMap<Socket, Integer> socketCount = new HashMap<>();

    public Server() {
    }

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("[ Waiting ]\n");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("Connected " + socket.getLocalPort() + " Port, From " + socket.getRemoteSocketAddress().toString() + "\n");
                // Thread
                handleClient(socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        System.out.println("start handle Client");
        try {
            while(true){
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

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

                actionByType(messageType, message, clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionByType(MessageType inputType, String message, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message);
        switch (inputType){
            case REGISTER_ID :
                String id = message;
                // LOCK
                if(idSocket.containsKey(id)){
                    MessageType sendingType = MessageType.ALREADY_EXIST;
                    sendTypeOnly(sendingType, clientSocket);
                    break;
                }
                idSocket.put(id, clientSocket);
                socketId.put(clientSocket, id);
                socketCount.put(clientSocket, 0);

                MessageType sendingType = MessageType.REGISTER_SUCCESS;
                System.out.println("register Success!! : " + id);
                sendTypeOnly(sendingType, clientSocket);
                break;

            case COMMENT:
                // lock
                socketCount.put(clientSocket, socketCount.get(clientSocket) + 1);
                MessageType sendingType1 = MessageType.COMMENT;
                String idAndMessage = socketId.get(clientSocket) + ": " + message;
                sendMessageToAllClient(sendingType1, idAndMessage);
                break;
            case QUIT:
                String quitSocketId = socketId.get(clientSocket);
                Integer quitSocketCount = socketCount.get(clientSocket);
                String outMessage = "ID:" + quitSocketId + "is out \n total message: " + quitSocketCount;
                MessageType sendingType2 = MessageType.NOTICE;

                idSocket.remove(quitSocketId);
                socketCount.remove(clientSocket);
                socketId.remove(clientSocket);
                clientSocket.close();

                sendMessageToAllClient(sendingType2, outMessage);
        }
    }


    private void sendMessageToAllClient(MessageType type, String message){
        for(Socket client : socketId.keySet()){
            try {
                OutputStream outputStream = client.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                byte[] sendingByte = Share.getSendPacketByteWithHeader(type, message);
                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendTypeOnly(MessageType type, Socket clientSocket) {
        try{
            OutputStream outputStream = clientSocket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            String message = "";
            byte[] sendingByte = Share.getSendPacketByteWithHeader(type, message);

            dataOutputStream.writeInt(sendingByte.length);
            dataOutputStream.write(sendingByte, 0, sendingByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}