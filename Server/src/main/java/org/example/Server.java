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
    final HashMap<Socket, DataOutputStream> SocketoutStreamMap = new HashMap<>();
    private final Object socketIdLock = new Object();

    private final Object socketCountLock = new Object();

    private final Object socketOutStreamLock = new Object();

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
                synchronized ( socketOutStreamLock ){
                    clientOutStream = SocketoutStreamMap.get(clientSocket);
                }
                // LOCK
                synchronized ( socketIdLock ){
                    if (!idSocketMap.containsKey(id)){
                        saveId(id, clientSocket);
                        System.out.println("register Success!! : " + id);
                        sendTypeOnly(MessageType.REGISTER_SUCCESS, clientOutStream);
                        registerSuccess = true;
                    } else {
                        sendTypeOnly(MessageType.ALREADY_EXIST_ID, clientOutStream);
                    }
                }
                if (registerSuccess){
                    synchronized ( socketMessageCountMap ){
                        setSocketMessageCountMap(clientSocket);
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
                socketDataRemove(clientSocket);
                clientSocket.close();
                break;
        }
    }


    private void saveId(String id, Socket socket){
        idSocketMap.put(id, socket);
        socketIdMap.put(socket, id);
    }

    private void setSocketMessageCountMap(Socket socket){
        socketMessageCountMap.put(socket, 0);
    }
    private void addSocketMessageCount(Socket socket){
        socketMessageCountMap.put(socket, socketMessageCountMap.get(socket) + 1);
    }

    private void socketDataRemove(Socket socket){
        String id = socketIdMap.get(socket);
        socketIdMap.remove(socket);
        idSocketMap.remove(id);
        socketMessageCountMap.remove(socket);
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
                dataOutputStream = SocketoutStreamMap.get(client);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(type, message);
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