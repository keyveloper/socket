package org.example;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Server{
    public final int tcpServerPort = Share.portNum;

    private final IdManager idManager = new IdManager(this);

    private final CountManager countManager = new CountManager(this);

    private final HashMap<Socket, ClientHandler> handlerMap = new HashMap<>();

    private final Object handlerLock = new Object();

    public void start(){
        try{
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(new InetSocketAddress(tcpServerPort));
                System.out.println("Starting tcp Server: " + tcpServerPort);
                System.out.println("[ Waiting ]\n");
                boolean isRunning = true;
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    synchronized ( handlerLock ) {
                        handlerMap.put(clientSocket, clientHandler);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processMessage(Message message, Socket senderSocket){
        switch (message.getMessageType()){
            case REGISTER_ID :
                resisterId(new String(message.getBody()), senderSocket);
                break;
            case COMMENT:
                sendComment(new String(message.getBody()),senderSocket);
                break;
            case CHANGE_ID:
                changeID(new String(message.getBody()), senderSocket);
                break;
            case WHISPER:
                sendWhisper(new String(message.getBody()), senderSocket);
                break;
            case FILE:
                sendFile(message.getBody());
                break;
            case FILE_END:
                sendFileEnd(message.getBody());
                break;
            case FIN:
                noticeFin(senderSocket);
                break;
        }
    }

    private void resisterId(String id, Socket socket){
        if (idManager.register(id, socket)){
            System.out.println("Id Reigsterd complete");
            countManager.register(socket);
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.REGISTER_SUCCESS);
                System.out.println("send ResisterSuceess");
            }
        } else {
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.ALREADY_EXIST_ID);
                System.out.println("Already Exist ID");
            }
        }

    }

    private void sendFileEnd(byte[] body) {
        // body = idLength id fileName
        System.out.println("sendFile body in end: " + Arrays.toString(body));
        ByteBuffer byteBuffer = ByteBuffer.wrap(body);

        // idlength(4) + id + fileNameLength(4) + fileName + seq + filebyte
        int idLengthSize = Integer.BYTES;
        int idLength = byteBuffer.getInt();
        byte[] idByte = new byte[idLength];
        byteBuffer.get(idByte);
        String receiverId = new String(idByte, StandardCharsets.UTF_8);
        System.out.println("receiver id: " + receiverId);

        byte[] remainBody = new byte[body.length - idLengthSize - idByte.length];
        byteBuffer.get(remainBody);
        System.out.println("remianBody(with filename)" + Arrays.toString(remainBody));

        Socket receiverSocket = idManager.getSocketById(receiverId);
        ClientHandler receiverHandler;
        synchronized (handlerLock) {
            receiverHandler = handlerMap.get(receiverSocket);
        }
        receiverHandler.sendPacket(MessageTypeLibrary.FILE_END, remainBody);
    }

    private void changeID(String id, Socket socket){
        String oldId = idManager.getIdBySocket(socket);
        if (idManager.changeId(id, socket)){
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.REGISTER_SUCCESS);

                for (Socket key : handlerMap.keySet()){
                    ClientHandler handler = handlerMap.get(key);
                    handler.sendPacket(MessageTypeLibrary.COMMENT, getIdChangeMessage(oldId, socket).getBytes());
                }

            }
        } else {
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageTypeLibrary.ALREADY_EXIST_ID);
                System.out.println("Already Exist ID");
            }
        }
    }

    private String getIdChangeMessage(String oldId, Socket socket) {
        return oldId + " changed ID: " + oldId + "  ->  " + idManager.getIdBySocket(socket);
    }

    private void sendWhisper(String message, Socket socket) {
        System.out.println("start send whisper");
        String[] parts = message.split(" ", 2);
        String receiverId = parts[0];
        String whisperMessage = makeWhisperMessage(parts[1], socket);
        Socket receiverSocket = idManager.getSocketById(receiverId);

        synchronized ( handlerLock ){
            handlerMap.get(receiverSocket).sendPacket(MessageTypeLibrary.WHISPER, whisperMessage.getBytes());
        }
    }

    private void sendFile(byte[] body) {
        System.out.println("\nStrat sendFile!!");
        //System.out.println("Test packet Received!!" + Arrays.toString(body));
        ByteBuffer byteBuffer = ByteBuffer.wrap(body);

        int idLengthSize = 4;
        int idLength = byteBuffer.getInt();
        byte[] idByte = new byte[idLength];
        byteBuffer.get(idByte);
        System.out.println(Arrays.toString(body));
        String receiveId = new String(idByte, StandardCharsets.UTF_8);

        byte[] packet = new byte[body.length - idLengthSize - idLength];
        byteBuffer.get(packet);
        System.out.println("remian body: " + Arrays.toString(packet));

        Socket receiverSocket = idManager.getSocketById(receiveId);
        ClientHandler receiverHandler;
        synchronized (handlerLock) {
            receiverHandler = handlerMap.get(receiverSocket);
        }
        receiverHandler.sendFile(packet);
    }

    private void noticeFin(Socket socket){
        String outMessage = makeSocketOutMessage(socket);
        removeData(socket);
        synchronized ( handlerLock ){
            for (Socket key : handlerMap.keySet()){
                ClientHandler handler = handlerMap.get(key);
                handler.sendPacket(MessageTypeLibrary.NOTICE, outMessage.getBytes());
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeData(Socket socket){
        idManager.remove(socket);
        countManager.remove(socket);
        synchronized ( handlerMap ){
            handlerMap.remove(socket);
        }
    }

    private String makeSocketOutMessage(Socket socket){
        // deadLock?
        String id = idManager.getIdBySocket(socket);
        int count = countManager.get(socket);
        return "Id: " + id +"is out \ntotal message count: " + count;
    }

    private String makeCommentMessage(Socket socket, String message){
        return idManager.getIdBySocket(socket) + " : " + message;
    }

    private void sendComment(String message, Socket socket){
        message = makeCommentMessage(socket, message);
        synchronized ( handlerLock ){
            for (Socket key : handlerMap.keySet()){
                ClientHandler handler = handlerMap.get(key);
                handler.sendPacket(MessageTypeLibrary.COMMENT, message.getBytes());
            }
        }
        countManager.add(socket);
    }

    private String makeWhisperMessage(String message, Socket socket){
        return "(whisper)" + idManager.getIdBySocket(socket) + ": " + message;
    }

}