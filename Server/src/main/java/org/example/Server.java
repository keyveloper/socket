package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    public final int tcpServerPort = Share.portNum;

    private final IdManager idManager = new IdManager(this);

    private final CountManager countManager = new CountManager(this);

    private final HashMap<Socket, ClientHandler> handlerMap = new HashMap<>();

    private final Object handlerLock = new Object();

    private boolean isRunning = true;
    public Server() {
    }

    public void start(){
        try{
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(new InetSocketAddress(tcpServerPort));
                System.out.println("Starting tcp Server: " + tcpServerPort);
                System.out.println("[ Waiting ]\n");
                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                    ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    synchronized ( handlerLock ){
                        handlerMap.put(clientSocket, clientHandler);
                    }
                    // 종료 구현
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void processMessage(Message message){
        System.out.println("message received: " + message.getBody() + " " + message.getMessageType());
        switch (message.getMessageType()){
            case REGISTER_ID :
                resisterId(message.getBody(), message.getClientSocket());
                break;
            case COMMENT:
                sendComment(message.getBody(), message.getClientSocket());
                break;
            case CHANGE_ID:
                changeID(message.getBody(), message.getClientSocket());
            case WHISPER:
                sendWhisper(message.getBody(), message.getClientSocket());
            case FIN:
                noticeFin(message.getClientSocket());
                break;
        }
    }

    private void resisterId(String id, Socket socket){
        if (idManager.register(id, socket)){
            System.out.println("Id Reigsterd complete");
            countManager.register(socket);
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageType.REGISTER_SUCCESS);
            }
        } else {
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageType.ALREADY_EXIST_ID);
                System.out.println("Already Exist ID");
            }
        }

    }

    private void changeID(String id, Socket socket){
        if (idManager.changeId(id, socket)){
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageType.REGISTER_SUCCESS);
            }
        } else {
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageType.ALREADY_EXIST_ID);
                System.out.println("Already Exist ID");
            }
        }
    }

    private void sendWhisper(String message, Socket socket){
        String[] parts = message.split(" ", 2);
        String receiverId = parts[0];
        String whisperMessage = makeWhisperMessage(parts[1], socket);
        Socket receiverSocket = idManager.getSocketById(receiverId);

        synchronized ( handlerLock ){
            handlerMap.get(receiverSocket).sendPacket(MessageType.WHISPER, whisperMessage);
        }
    }

    private void noticeFin(Socket socket){
        synchronized ( handlerLock ){
            handlerMap.get(socket).sendPacket(MessageType.NOTICE, makeSocketOutMessage(socket));
        }
        removeData(socket);
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
        return "Id: " + id +"\ntotal message count: " + count;
    }

    private String makeCommentMessage(Socket socket, String message){
        return idManager.getIdBySocket(socket) + " : " + message;
    }

    private void sendComment(String message, Socket socket){
        message = makeCommentMessage(socket, message);
        synchronized ( handlerLock ){
            for (Socket key : handlerMap.keySet()){
                ClientHandler handler = handlerMap.get(key);
                handler.sendPacket(MessageType.COMMENT, message);
            }
        }
    }

    private String makeWhisperMessage(String message, Socket socket){
        return "(whisper)" + idManager.getIdBySocket(socket) + ": " + message;
    }
}