package org.example;

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    private Boolean registered = false;

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
                String bodyMessage = seperateBodyMessage(command);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);

                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();

                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0){
                    //[]
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    int messageLength = Share.readInputLength(inLengthByte);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    MessageType messageType1 = Share.readInputType(inTypeByte);

                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String message = Share.readInputMessage(inMessageByte);

                    actionByType(messageType1, message);
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
        switch (inputType){
            case COMMENT:
                System.out.println("\n" + message + "\n");
                break;
            case NOTICE:
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

    private MessageType getMessageTypeByCommand(String command){
        if(command.startsWith("/R")){
            return MessageType.REGISTER_ID;
        }else if(command.startsWith("/Q")){
            return MessageType.FIN;
        }
        return MessageType.COMMENT;
    }

    private String seperateBodyMessage(String command){
        String bodyMessage;
        if(command.startsWith("/R")){
            bodyMessage = command.substring(  3);
            return bodyMessage;
        }else if(command.startsWith("/Q")){
            bodyMessage = "";
            return bodyMessage;
        }else if(command.startsWith("/")){
            return null;
        }
        bodyMessage = command;
        return bodyMessage;
    }


}