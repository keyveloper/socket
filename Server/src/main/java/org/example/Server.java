package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class Server {
    public final static int tcpServerPort = 9999;
    private MessageType messageType = MessageType.COMMENT;
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

                actionByType(message, messageType, clientSocket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void actionByType(String message, MessageType type, Socket clientSocket) throws IOException {
        System.out.println("message received: " + message);
        boolean valid= validSocket(clientSocket);
        switch (type){
            case REGISTER_ID :
                String id = message;
                // LOCK
                if(idSocket.containsKey(id)){
                    MessageType messageType = MessageType.ALREADY_EXIST;
                    sendTypeOnly(clientSocket, messageType);
                    break;
                }
                idSocket.put(id, clientSocket);
                socketId.put(clientSocket, id);
                socketCount.put(clientSocket, 0);

                MessageType messageType = MessageType.REGISTER_SUCCESS;
                System.out.println("register Success!! : " + id);
                sendTypeOnly(clientSocket, messageType);
                break;

            case COMMENT:
                // lock
                socketCount.put(clientSocket, socketCount.get(clientSocket) + 1);

                break;
            case QUIT:

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

    private void sendTypeOnly(Socket clientSocket, MessageType type) {
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



    private boolean validSocket(Socket clientSocket){
        if (clientInfo.containsKey(clientSocket)){
            return true;
        }
        return false;
    }

}