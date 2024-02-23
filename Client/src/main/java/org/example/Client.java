package org.example;

import java.io.*;
import java.net.*;

public class Client implements Runnable {
    Boolean registered = false;

    public void run(){
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);

        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", 9999));
            System.out.print("\n [ Success connecting ] \n");
            Thread inputDatahandler = new Thread(new InputDataHandler(this, socket));
            inputDatahandler.start();

            while (true) {
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

                String command = bufferedReader.readLine();
                MessageType messageType = getMessageTypeByCommand(command);
                String bodyMessage = seperateBodyMessage(command);
                byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);

                dataOutputStream.writeInt(sendingByte.length);
                dataOutputStream.write(sendingByte, 0, sendingByte.length);
                dataOutputStream.flush();

                // 종료 구현해야함
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private MessageType getMessageTypeByCommand(String command){
        if(command.startsWith("/R")){
            return MessageType.REGISTER_ID;
        }else if(command.startsWith("/Q")){
            return MessageType.FIN_CLIENT;
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

    void printInputData(String message){
        System.out.println(message);
    }


}