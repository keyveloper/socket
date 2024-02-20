package org.example;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.regex.*;

public class Client implements Runnable {
    private Boolean register = false;

    private MessageType messageType = MessageType.COMMENT;

    long threadId = Thread.currentThread().getId();

    public void run(){
        System.out.println("myid: "+  threadId);
        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.print("\n [ Success connecting ] \n");
            InputStream inputStream = socket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            while(true){
                String command = bufferedReader.readLine();
                boolean validCommand = checkCommand(command);
                String message = "";
                byte[] messageByte = null;

                if(command.startsWith("/") && validCommand){
                    if (command.startsWith("/register")){
                        message = command.substring(10);
                        messageType = MessageType.REGISTER_ID;
                        messageByte = Share.getHeaderPacketByte(message.getBytes(), messageType);
                        sendPacket(messageByte, this.messageType, socket);
                    } else if(command.startsWith("/quit")){
                        messageType = MessageType.QUIT;
                        messageByte = Share.getHeaderPacketByte("".getBytes(), messageType);
                        sendPacket(messageByte, this.messageType, socket);
                        socket.close();
                    }
                } else{
                    messageType = MessageType.COMMENT;
                    messageByte = Share.getHeaderPacketByte(command.getBytes(), messageType);
                    sendPacket(messageByte, this.messageType, socket);
                }


                // input stream 관리
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


    public void sendPacket(byte[] messageByte, MessageType messageType, Socket socket) throws IOException {
        try{
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeInt(messageByte.length);
            dataOutputStream.write(messageByte, 0, messageByte.length);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void actionByType(String message, int typeInt){
        if(typeInt == 1){
            System.out.println(threadId + " - " + message);
        }else if(typeInt == 3){
            this.register = true;
        }else if(typeInt == 4){
            System.out.println("Already exist ID");
        }else if(typeInt == 5){
            System.out.println("Register first \n /register your id");
        }
    }

    public boolean checkCommand(String command){
        String validCommandPattern = "/(register|quit)";
        Pattern pattern= Pattern.compile(validCommandPattern);
        Matcher matcher = pattern.matcher(command);
        if(matcher.find()){
            return true;
        }
        return false;
    }
}