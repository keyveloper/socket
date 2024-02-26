package org.example;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client implements Runnable {

    private final int tcpClientPort = Share.portNum;
    private Boolean registered = false;

    private ArrayList<String> commandList = new ArrayList<>();

    public Client(){
        commandList.add("R");
        commandList.add("Q");
    }

    public void run(){
        long threadId = Thread.currentThread().getId();
        System.out.println("myid: "+  threadId);

        Socket socket = new Socket();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            System.out.println("\n[ Request ... ]");
            socket.connect(new InetSocketAddress("localhost", tcpClientPort));
            System.out.print("\n [ Success connecting ] \n");

            while(true){
                OutputStream outputStream = socket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                String command = bufferedReader.readLine();
                MessageType messageType = getMessageTypeByCommand(command);

                if (messageType == null){
                    System.out.println("존재하지 않는 명령어");
                    continue;
                } else if (registered && messageType != MessageType.REGISTER_ID){
                    String bodyMessage = seperateBodyMessage(command);
                    byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);                    dataOutputStream.writeInt(sendingByte.length);
                    dataOutputStream.write(sendingByte, 0, sendingByte.length);
                    dataOutputStream.flush();
                } else if(messageType == MessageType.REGISTER_ID){
                    String bodyMessage = seperateBodyMessage(command);
                    byte[] sendingByte = Share.getSendPacketByteWithHeader(messageType, bodyMessage);                    dataOutputStream.writeInt(sendingByte.length);
                    dataOutputStream.write(sendingByte, 0, sendingByte.length);
                    dataOutputStream.flush();
                }  else {
                    System.out.println("Register first! \n command : /R");
                    continue;
                }

                int inAllLength = dataInputStream.readInt();
                if(inAllLength > 0){
                    //[]
                    byte[] inLengthByte = new byte[4];
                    dataInputStream.readFully(inLengthByte);
                    int messageLength = Share.readInputLength(inLengthByte);

                    byte[] inTypeByte = new byte[4];
                    dataInputStream.readFully(inTypeByte);
                    MessageType inMessageType = Share.readInputType(inTypeByte);

                    byte[] inMessageByte = new byte[inAllLength - 8];
                    dataInputStream.readFully(inMessageByte);
                    String message = Share.readInputMessage(inMessageByte);

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
        }else if(command.startsWith("/")){
            return null;
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
        }
        bodyMessage = command;
        return bodyMessage;
    }


}