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

    private Socket clientSocket = null;

    public Server() {
        try {
            ServerSocket serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(tcpServerPort));
            System.out.println("Starting tcp Server: " + tcpServerPort);
            System.out.println("\n [Waiting] \n");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Connected" + clientSocket.getLocalPort() + "Port, From" + clientSocket.getRemoteSocketAddress() + "\n");

                // stream
                OutputStream outputStream = clientSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                // input
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
                String receivedMessage = new String(inMessageByte);

                System.out.println("receiveMessage : " + receivedMessage);
                action(receivedMessage, typeInt);
            }
        } catch (IOException io) {
            io.getStackTrace();
        }
    }
    //

    private void action(String message, int typeInt){
        if(typeInt == 0){
            if(idList.containsKey(message)) {
                messageType = MessageType.REJECT;
                return;
            }
            idList.put(message, clientSocket);
            messageType = MessageType.SUCCESS;
        } else if(typeInt == 1){
            // 저장 어떻게 할지 고민좀
        }
    }

}