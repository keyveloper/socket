package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public final static int tcpServerPort = 9999;
    private final HashMap<String, Socket> idSocketMap = new HashMap<>();
    private final HashMap<Socket, String> socketIdMap = new HashMap<>();
    private final HashMap<Socket, Integer> socketMessageCountMap = new HashMap<>();

    private final HashMap<Socket, DataOutputStream> outputStreamMap = new HashMap<>();

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
                OutputStream outputStream = clientSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                synchronized (outputStreamMap){
                    outputStreamMap.put(clientSocket, dataOutputStream);
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

                actionByType(messageType, message, clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionByType(MessageType inputType, String message, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message + inputType);
        switch (inputType){
            case REGISTER_ID :
                String id = message;
                // LOCK
                if (checkOriginalId(id)){
                    saveId(id, clientSocket);
                    System.out.println("register Success!! : " + id);
                    sendTypeOnly(MessageType.REGISTER_SUCCESS, clientSocket);
                } else {
                    sendTypeOnly(MessageType.ALREADY_EXIST, clientSocket);
                }
                break;
            case COMMENT:
                addMessageCount(clientSocket);
                sendCommentToAllClient(MessageType.COMMENT, message, clientSocket);
                break;
            case FIN:
                clientSocket.close();
                sendNoticeToAllClient(MessageType.NOTICE, getSocketOutMessage(clientSocket));
                socketDataRemove(clientSocket);
                break;
        }
    }

    private boolean checkOriginalId(String id){
        synchronized (idSocketMap){
            if (idSocketMap.containsKey(id)){
                return false;
            } else {
                return  true;
            }
        }
    }

    private void saveId(String id, Socket socket){
        synchronized (idSocketMap){
            idSocketMap.put(id, socket);
        }
        synchronized (socketIdMap){
            socketIdMap.put(socket, id);
        }

        socketMessageCountMap.put(socket, 0);
    }



    private void addMessageCount(Socket socket){
        socketMessageCountMap.put(socket, socketMessageCountMap.get(socket) + 1);
    }

    private void socketDataRemove(Socket socket){
        String id = socketIdMap.get(socket);
        socketIdMap.remove(socket);
        idSocketMap.remove(id);
        socketMessageCountMap.remove(socket);
    }

    private String getSocketOutMessage(Socket socket){
        return "ID:" + socketIdMap.get(socket) + "is out \n total message count: " + socketMessageCountMap.get(socket);
    }

    private void sendCommentToAllClient(MessageType type, String message, Socket sendingSocket){
        String idAndMessage = socketIdMap.get(sendingSocket) + " : " + message;

        for(Socket client : socketIdMap.keySet()){
            try {
                DataOutputStream dataOutputStream;
                synchronized (outputStreamMap){
                    dataOutputStream = outputStreamMap.get(client);
                }
                byte[] sendingByte = Share.getSendPacketByteWithHeader(type, idAndMessage);
                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendNoticeToAllClient(MessageType type, String message){
        for(Socket client : socketIdMap.keySet()){
            try {
                DataOutputStream dataOutputStream;
                synchronized (outputStreamMap){
                    dataOutputStream = outputStreamMap.get(client);
                }
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
            DataOutputStream dataOutputStream;
            synchronized (outputStreamMap){
                dataOutputStream = outputStreamMap.get(clientSocket);
            }
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