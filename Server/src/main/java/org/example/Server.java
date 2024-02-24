package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Server{
    public final static int tcpServerPort = 9999;
    private final HashMap<String, Socket> idSocketMap = new HashMap<>();
    private final HashMap<Socket, String> socketIdMap = new HashMap<>();
    private final HashMap<Socket, Integer> socketMessageCountMap = new HashMap<>();
    final HashMap<Socket, DataOutputStream> socketoutStreamMap = new HashMap<>();
    private final Object socketIdLock = new Object();

    private final Object socketCountLock = new Object();

    final Object socketOutStreamLock = new Object();

    public Server() {
    }

    public void start(){
        try{
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("[ Waiting ]\n");

            while (true){
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected " + clientSocket.getLocalPort() + " Port, From " + clientSocket.getRemoteSocketAddress().toString() + "\n");

                Thread clientHandler = new Thread(new ClientHandler(this, clientSocket));
                clientHandler.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void actionByType(MessageType inputType, String message, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message + " " + inputType);
        switch (inputType){
            case REGISTER_ID :
                String id = message;
                Boolean registerSuccess = false;
                DataOutputStream clientOutStream;
                MessageType messageType;

                // LOCK
                synchronized ( socketIdLock ){
                    if (!idSocketMap.containsKey(id)){
                        saveId(id, clientSocket);
                        messageType = MessageType.REGISTER_SUCCESS;
                        System.out.println("register Success!! : " + id);
                        registerSuccess = true;
                    } else {
                        messageType = MessageType.ALREADY_EXIST_ID;
                    }
                }
                if (registerSuccess){
                    synchronized ( socketMessageCountMap ){
                        System.out.println("set socketCount!");
                        addSocketMessageCount(clientSocket);
                        System.out.println("set end!!!");
                    }
                    synchronized ( socketOutStreamLock ){
                        clientOutStream = socketoutStreamMap.get(clientSocket);
                        sendTypeOnly(messageType, clientOutStream);
                    }
                }
                break;
            case COMMENT:
                String idAndMessage;
                synchronized ( socketIdLock ){
                    idAndMessage = socketIdMap.get(clientSocket) + " : " + message;
                }
                synchronized ( socketOutStreamLock ){
                    sendPacketToAllClient(MessageType.COMMENT,idAndMessage);
                }
                synchronized ( socketCountLock ){
                    addSocketMessageCount(clientSocket);
                }
                break;
            case FIN:
                String outMessage = getSocketOutMessage(clientSocket);
                synchronized ( socketOutStreamLock ){
                    sendPacketToAllClient(MessageType.NOTICE, outMessage);
                }
                synchronized ( socketIdLock ){
                    socketIdRemove(clientSocket);
                }

                synchronized ( socketCountLock ){
                    socketCountRemove(clientSocket);
                }

                synchronized ( socketOutStreamLock ){
                    socketOutstreamRemove(clientSocket);
                }

                clientSocket.close();
                break;
        }
    }


    private void saveId(String id, Socket socket){
        idSocketMap.put(id, socket);
        socketIdMap.put(socket, id);
    }

    private void addSocketMessageCount(Socket socket){
        if (socketMessageCountMap.containsKey(socket)){
            socketMessageCountMap.put(socket, socketMessageCountMap.get(socket) + 1);
        } else {
            socketMessageCountMap.put(socket, 0);
        }
    }

    private void socketIdRemove(Socket socket){
        String id = socketIdMap.get(socket);
        socketIdMap.remove(socket);
        idSocketMap.remove(id);
    }

    private void socketCountRemove(Socket socket){
        socketMessageCountMap.remove(socket);
    }

    private void socketOutstreamRemove(Socket socket){
        socketoutStreamMap.remove(socket);
    }

    private String getSocketOutMessage(Socket socket){
        String head;
        String tail;
        synchronized ( socketIdLock ){
            head = "ID:" + socketIdMap.get(socket) + "is out \n";
        }

        synchronized ( socketCountLock ){
            tail = "total message count: " + socketMessageCountMap.get(socket);
        }
        return head + tail;
    }

    private void sendPacketToAllClient(MessageType messageType,String message){
        // 이것도 LOCK필요
        for(Socket client : socketIdMap.keySet()){
            try {
                DataOutputStream dataOutputStream;
                dataOutputStream = socketoutStreamMap.get(client);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, message);
                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendTypeOnly(MessageType type, DataOutputStream stream) {
        try{
            DataOutputStream dataOutputStream = stream;
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