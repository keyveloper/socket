package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Server {
    public final static int tcpServerPort = 9999;
    private MessageType messageType = MessageType.COMMENT;
    private final HashMap<String, Socket> idList = new HashMap<>();
    private final HashMap<Socket, HashMap<String, Integer>> clientInfo = new HashMap<>();

    private ArrayList<Socket> clientSocketList = new ArrayList<>();


    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("\n [Waiting] \n");
            while (true) {
                System.out.println("Server whiling...");
                Socket clientSocket = serverSocket.accept();
                clientSocketList.add(clientSocket);
                System.out.println("Connected" + clientSocket.getLocalPort() + "Port, From" + clientSocket.getRemoteSocketAddress() + "\n");

                // client별로 inputsteam을 처리
                new Thread(() -> {
                    try {
                        handleClient(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();

                // input
                // output
            }
        } catch (IOException io) {
            io.getStackTrace();
        }
    }
    //

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
                int inMessageLength = ByteBuffer.wrap(inLengthByte).getInt();
                System.out.println("message length: " + inMessageLength);

                //byte[4] = type
                byte[] inTypeByte = new byte[4];
                dataInputStream.readFully(inTypeByte);
                ByteBuffer typeBuffer = ByteBuffer.wrap(inTypeByte);
                int typeInt = typeBuffer.getInt();
                System.out.println("received type int :  " + typeInt);

                //byte[n] = body
                byte[] inMessageByte = new byte[inAllLength - 8];
                dataInputStream.readFully(inMessageByte);
                String inMessage = new String(inMessageByte);
                actionByType(inMessage, typeInt, clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToAllClient(String message) throws IOException {
        messageType = MessageType.COMMENT;
        for(Socket client : clientSocketList){
            OutputStream outputStream = client.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            try {
                byte[] outMessageByte = Share.getHeaderPacketByte(message.getBytes(), messageType);
                dataOutputStream.writeInt(outMessageByte.length);
                dataOutputStream.write(outMessageByte, 0, outMessageByte.length);
                dataOutputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendTypeOnly(Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        try{
            String outMessage = "";
            byte[] outMessageByte = Share.getHeaderPacketByte(outMessage.getBytes(), messageType);
            dataOutputStream.writeInt(outMessageByte.length);
            dataOutputStream.write(outMessageByte, 0, outMessageByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void actionByType(String message, int typeInt, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message);
        boolean valid= validSocket(clientSocket);
        if(typeInt == 0){
            if(idList.containsKey(message)){
                messageType = MessageType.ALREADY_EXIST;
                sendTypeOnly(clientSocket);
                return;
            } else if (clientInfo.containsKey(clientSocket)){
                return;
            }
            idList.put(message, clientSocket);
            HashMap<String, Integer> newIdCount = new HashMap<>();
            newIdCount.put(message, 0);
            clientInfo.put(clientSocket, newIdCount);

            messageType = MessageType.REGISTER_SUCCESS;
            System.out.println("register Success!! : "+ message);
            sendTypeOnly(clientSocket);

        } else if(typeInt == 1 && valid){
            System.out.println("action By Type : 1" + message);
            HashMap<String, Integer> oldIdCount = clientInfo.get(clientSocket);
            // oldIdCount = {"id": 0}
            System.out.println("e");
            String id = "";
            Set<String> keys = oldIdCount.keySet();
            for(String key: keys){
                id = key;
            }
            System.out.println("id: " + id);
            oldIdCount.put(id, oldIdCount.get(id) + 1);
            clientInfo.put(clientSocket, oldIdCount);

            String newMessage = id + ": " + message;
            sendMessageToAllClient(newMessage);
        } else if(typeInt == 2 && valid){
            HashMap<String, Integer> quitClient = clientInfo.get(clientSocket);
            Map.Entry<String, Integer> entry = quitClient.entrySet().iterator().next();
            String id = entry.getKey();
            int count = entry.getValue();
            String notice = "id: " + id + "is out this chat room" + "\n" + "total message: " + count;
            clientSocket.close();
            sendMessageToAllClient(notice);
        } else{
            messageType = MessageType.REJECT;
            sendTypeOnly(clientSocket);
        }
    }

    private boolean validSocket(Socket clientSocket){
        if (clientInfo.containsKey(clientSocket)){
            return true;
        }
        return false;
    }

}