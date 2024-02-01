package org.example;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client implements Runnable {
    private Boolean register = false;

    private MessageType messageType = MessageType.COMMENT;

    public void run(){
        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.print("\n [ Success ... ]");

            while(true){
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                byte[] messageBytes = null;
                String message = null;

                String command = bufferedReader.readLine();
                if (command.startsWith("/register")){
                    message = command.substring(10);
                    messageType = MessageType.REGISTER_ID;
                    messageBytes = getBytePacket(message);

                } else if(command.startsWith("/quit")){
                    message = command.substring(6);
                    messageType = MessageType.QUIT;
                    messageBytes = getBytePacket(message);

                } else{
                    messageType = MessageType.COMMENT;
                    messageBytes = getBytePacket(command);
                }

                dataOutputStream.writeInt(messageBytes.length);
                dataOutputStream.write(messageBytes, 0, messageBytes.length);
                dataOutputStream.flush();

                System.out.println("\n[ Data Send Success ]\n" + message);

                // input
                int receiveLength = dataInputStream.readInt();
                if(receiveLength > 0){
                    // type별로 나누기
                    byte receiveByte[] = new byte[receiveLength];
                    dis.readFully(receiveByte, 0, receiveLength);
                    System.out.println("\n[ Data Receive Success ]\n" + message);
                } else if(receiveLength == 0){
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getHeader(byte[] packetBytes){
        return Share.plusHeader(packetBytes, messageType);
    }

    public byte[] getBytePacket(String message){
        byte[] messageBytes = message.getBytes();
        byte[] finalBytes = getHeader(messageBytes);
        return finalBytes;
    }
}