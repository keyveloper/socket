package org.example;

import javax.swing.text.Style;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class Server {
    public final static int tcpServerPort = 9999;
    private MessageType messageType = MessageType.SUCCESS;
    private final HashMap<String, Socket> idList = new HashMap<>();
    private final HashMap<Socket, Integer> ClientMessageCount = new HashMap<>();

    private ArrayList<Socket> clientSocketList = new ArrayList<>();


    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("\n [Waiting] \n");
            while (true) {
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
        try {
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
            int inTypeeLength = ByteBuffer.wrap(inLengthByte).getInt();
            int typeInt = TypeChange.byteArrayToIntLittleEndian(inTypeByte);
            System.out.println("type length: " + inMessageLength);

            //byte[n] = body
            byte[] inMessageByte = new byte[inAllLength - 8];
            dataInputStream.readFully(inMessageByte);
            String inMessage = new String(inMessageByte);

            System.out.println("receiveMessage : " + inMessage);
            action(inMessage, typeInt, clientSocket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMessageToAllClient(String message){
        for(Socket socket : clientSocketList){
            try {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println("Server broadcast: " + message);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendTypeOnly(Socket clientSocket){
        try{
            String outMessage = null;
            byte[] outMessageByte = Share.getHeaderPacketByte(outMessage.getBytes(), messageType);
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            writer.println("Server send only type" + messageType);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void action(String message, int typeInt, Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        if(typeInt == 0){
            if(idList.containsKey(message)) {
                messageType = MessageType.REJECT;
                return;
            }
            idList.put(message, clientSocket);
            messageType = MessageType.SUCCESS;
            sendTypeOnly(clientSocket);

        } else if(typeInt == 1){
            sendMessageToAllClient(message);
            messageType = MessageType.COMMENT;
        } else if(typeInt == 2){
            clientSocket.close();
        }
    }

}