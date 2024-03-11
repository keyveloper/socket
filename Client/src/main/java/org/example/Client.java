package org.example;

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    private final int tcpClientPort = Share.portNum;
    private Boolean registered = false;
    private DataOutputStream dataOutputStream;

    Socket socket;

    public Client() {
        socket = new Socket();
    }
    public void run() {
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);
        try {
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n [ Success connecting ] \n");

            while (true) {
                OutputStream outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);


                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0) {
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    // int messageLength = Share.readInputLength(inLengthByte);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    MessageType inMessageType = Share.readInputType(inTypeByte);

                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String message = Share.convertString(inMessageByte);

                    actionByType(inMessageType, message);
                } else if(inAllLength == 0){
                    // quit
                    break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void actionByType(MessageType inputType, String message){
        switch (inputType) {
            case COMMENT, WHISPER, NOTICE:
                System.out.println("\n" + message + "\n");
                break;
            case ALREADY_EXIST_ID:
                System.out.println("This ID already Exist");
                break;
            case REGISTER_SUCCESS:
                System.out.println("Register Success!!");
                this.registered = true;
                break;
        }
    }

    public void processCommand(String command){
        try {
            MessageType messageType = getMessageTypeByCommand(command);
            System.out.println("command message type: " + messageType);
            if (messageType == null){
                System.out.println("wrong command");
            } else if (registered && messageType != MessageType.REGISTER_ID){
                String bodyMessage = seperateBodyMessage(command);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);                    dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } else if (messageType == MessageType.REGISTER_ID){
                String bodyMessage = seperateBodyMessage(command);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);                    dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();
            } else if (messageType == MessageType.FILE) {
                String[] parts = seperateBodyMessage(command).split(" ", 2);
                // part1 = id, part2 = file path
                String filepath = parts[1];

            } else if (messageType == MessageType.FIN){
                System.out.println("connection end");
                dataOutputStream.close();
                socket.close();
            } else {
                System.out.println("Register first! \n command : /R");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageType getMessageTypeByCommand(String command){
        if (command.startsWith("/R")){
            return MessageType.REGISTER_ID;
        } else if (command.startsWith("/Q")){
            return MessageType.FIN;
        } else if (command.startsWith("/N")) {
            return MessageType.CHANGE_ID;
        } else if (command.startsWith("/W")) {
            return MessageType.WHISPER;
        } else if (command.startsWith("/F")){
            return MessageType.FILE;
        } else if (command.startsWith("/")){
            return null;
        }
        return MessageType.COMMENT;
    }

    private String seperateBodyMessage(String command){
        String bodyMessage;
        if (command.startsWith("/Q")){
            return "";
        } else if (command.startsWith("/")) {
            return command.substring(3);
        }
        bodyMessage = command;
        return bodyMessage;
    }


}