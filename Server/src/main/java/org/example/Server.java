package org.example;

import com.sun.source.tree.Scope;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Server{
    public final int tcpServerPort = Share.portNum;

    private final IdManager idManager = new IdManager(this);

    final HashMap<Socket, DataOutputStream> socketoutStreamMap = new HashMap<>();


    final Object socketOutStreamLock = new Object();

    public Server() {
    }

    public void start(){
        try{
            try (ServerSocket serverSocket = new ServerSocket()) {
                serverSocket.bind(new InetSocketAddress(tcpServerPort));
                System.out.println("Starting tcp Server: " + tcpServerPort);
                System.out.println("[ Waiting ]\n");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                    Thread clientHandler = new Thread(new InputStreamHandler(this, clientSocket));
                    clientHandler.start();
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
                sendRegisterResultPacket(message, clientSocket);
                break;
            case COMMENT:
                sendComment(message, clientSocket);
                break;
            case FIN:
                noticeFin(clientSocket);
                removeData(clientSocket);
                clientSocket.close();
                break;
        }
    }

    private void sendRegisterResultPacket(String id, Socket socket){
        if (idManager.checkRegisterSuccess(id)){
            sendTypeOnly(MessageType.REGISTER_SUCCESS, socket);
        } else {
            sendTypeOnly(MessageType.ALREADY_EXIST_ID, socket);
        }
    }

    private void resisterId(String id, Socket socket){
        idManager.register(id, socket);
    }

    private void socketCountRemove(Socket socket){
        socketMessageCountMap.remove(socket);
    }

    private void socketOutstreamRemove(Socket socket){
        socketoutStreamMap.remove(socket);
    }

    private void noticeFin(Socket socket){
        sendPacketToAllClient(MessageType.NOTICE, getSocketOutMessage(socket));
    }

    private void removeData(Socket socket){
        idManager.removeSoket(socket);
    }

    private String getSocketOutMessage(Socket socket){
        return "Id: " + idManager.getIdBySocket(socket) +"\ntotal message count: " + getSocketMessageCount(socket);
    }

    private String makeCommentMessage(Socket socket, String message){
        return idManager.getIdBySocket(socket) + " : " + message;
    }

    private void sendComment(String message, Socket socket){
        message = makeCommentMessage(socket, message);
        sendPacketToAllClient(MessageType.COMMENT, message);
    }


    private void sendPacketToAllClient(MessageType messageType,String message){
        // 이것도 LOCK필요
        for(Socket client : socketoutStreamMap.keySet()){
            try {
                DataOutputStream dataOutputStream = socketoutStreamMap.get(client);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, message);
                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendTypeOnly(MessageType type, Socket socket) {
        synchronized ( socketOutStreamLock ){
            try{
                DataOutputStream dataOutputStream = socketoutStreamMap.get(socket);
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
}