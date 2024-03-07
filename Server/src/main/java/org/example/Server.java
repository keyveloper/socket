package org.example;

import com.sun.source.tree.Scope;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Handler;
import java.util.stream.Collectors;

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
        try {
            actionByType(message.getMessageType(), message.getBody(), message.getClientSocket());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionByType(MessageType inputType, String message, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message + " " + inputType);
        switch (inputType){
            case REGISTER_ID :
                resisterId(message, clientSocket);
                break;
            case COMMENT:
                sendComment(message, clientSocket);
                break;
            case FIN:
                noticeFin(clientSocket);
                break;
        }
    }

    private void resisterId(String id, Socket socket){
        idManager.register(id, socket);
        if (idManager.checkRegisterSuccess(id)){
            countManager.register(socket);
            synchronized ( handlerLock ){
                handlerMap.get(socket).sendTypeOnly(MessageType.REGISTER_SUCCESS);
            }
        }
        synchronized ( handlerLock ){
            handlerMap.get(socket).sendTypeOnly(MessageType.ALREADY_EXIST_ID);
        }
    }

    private void noticeFin(Socket socket){
        synchronized ( handlerLock ){
            handlerMap.get(socket).sendPacket(MessageType.NOTICE, getSocketOutMessage(socket));
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

    private String getSocketOutMessage(Socket socket){
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
}