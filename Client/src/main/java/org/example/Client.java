package org.example;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.*;

public class Client implements Runnable {

    public void run(){
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);

        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.print("\n [ Success connecting ] \n");

            while(true){
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String command = bufferedReader.readLine();
                MessageType messageType = getMessageTypeByCommand(command);
                String bodyMessage = seperateBody(command);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);

                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();

                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0){
                    //[]
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    int inMessageLength = ByteBuffer.wrap(inLengthByte).getInt();

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    ByteBuffer typeBuffer = ByteBuffer.wrap(inTypeByte);
                    int typeInt = typeBuffer.getInt();

                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String inMessage = new String(inMessageByte);

                    actionByType(inMessage, typeInt);
                } else if(inAllLength == 0){
                    // quit
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    public void actionByType(String message, int typeInt){
        if(typeInt == 1){
            System.out.println(threadId + " - " + message);
        }else if(typeInt == 3){
        }else if(typeInt == 4){
            System.out.println("Already exist ID");
        }else if(typeInt == 5){
            System.out.println("Register first \n /register your id");
        }
    }

    private MessageType getMessageTypeByCommand(String command){
        if(command.startsWith("/REGISTER")){
            return MessageType.REGISTER_ID;
        }else if(command.startsWith("/QUIT")){
            return MessageType.QUIT;
        }
        return MessageType.COMMENT;
    }

    private String seperateBody(String command){
        String bodyMessage;
        if(command.startsWith("/REGISTER")){
            bodyMessage = command.substring(10);
            return bodyMessage;
        }else if(command.startsWith("/QUIT")){
            bodyMessage = command.substring(6);
            return bodyMessage;
        }
        bodyMessage = command;
        return bodyMessage;
    }


}